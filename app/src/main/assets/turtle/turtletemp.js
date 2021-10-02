/*
 *  Copyright  2015 Google Inc. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

/**
 * @fileoverview Javascript for the Turtle Blockly demo on Android.
 * @author fenichel@google.com (Rachel Fenichel)
 */
'use strict';

/**
 * Create a namespace for the application.
 */
var Turtle = function(){
    /**
     * Uninitialized defaults for the stage size.
     */
    var STAGE_WIDTH = 400;
    var STAGE_HEIGHT = 400;

    /**
     * PID of animation task currently executing.
     */
    var pid = 0;

    /**
     * Should the turtle be drawn?
     */
    var visible = true;

    /**
     * Initialize Blockly and the turtle.  Called on page load.
     */
     var slider;

     var ctxDisplay;
     var ctxScratch;

     var x;
     var y;
     var heading;
     var penDownValue;

     var log=[];
     var ticks;

    var init = function() {
      // Speed controls
      slider = document.getElementById('slider');
      var slower = document.getElementById('slower');
      if (slower) {
        slower.onclick = function() {
          var newValue = Number(slider.value) - 10;
          slider.value = Math.max(newValue, slider.min);
        }
      }
      var faster = document.getElementById('faster');
      if (faster) {
        faster.onclick = function() {
          var newValue = Number(slider.value) + 10;
          slider.value = Math.min(newValue, slider.max);
        }
      }

      // Stage initialization
      STAGE_WIDTH = window.innerWidth - 20;   // page margin fudge factor
      STAGE_HEIGHT = window.innerHeight - 20; // page margin fudge factor

      var visualization = document.getElementById('visualization');
      visualization.innerHTML =
        '<canvas id="display" width="'+ STAGE_WIDTH + '" height="' + STAGE_HEIGHT +'"></canvas>'
        + '<canvas id="scratch" width="'+ STAGE_WIDTH + '" height="' + STAGE_HEIGHT + '" style="display: none"></canvas>';

      // Add to reserved word list: API, local variables in execution environment
      // (execute) and the infinite loop detection function.
      //Blockly.JavaScript.addReservedWords('Turtle,code');

      ctxDisplay = document.getElementById('display').getContext('2d');
      ctxScratch = document.getElementById('scratch').getContext('2d');
      reset();
    };

    window.addEventListener('load',init);

    /**
     * Reset the turtle to the start position, clear the display, and kill any
     * pending tasks.
     */
    var reset = function() {
      // Starting location and heading of the turtle.
       x = STAGE_WIDTH / 2;
       y = STAGE_HEIGHT / 2;
       heading = 0;
       penDownValue = true;
      visible = true;

      // Clear the display.
      ctxScratch.canvas.width = ctxScratch.canvas.width;
      ctxScratch.strokeStyle = '#000000';
      ctxScratch.fillStyle = '#000000';
      ctxScratch.lineWidth = 1;
      ctxScratch.lineCap = 'round';
      ctxScratch.font = 'normal 18pt Arial';
      display();

      // Kill any task.
      if (pid) {
        window.clearTimeout(pid);
      }
      pid = 0;
    };

    /**
     * Copy the scratch canvas to the display canvas. Add a turtle marker.
     */
    var display = function() {
      ctxDisplay.globalCompositeOperation = 'copy';
      ctxDisplay.drawImage(ctxScratch.canvas, 0, 0);
      ctxDisplay.globalCompositeOperation = 'source-over';
      // Draw the turtle.
      if (visible) {
        // Make the turtle the colour of the pen.
        ctxDisplay.strokeStyle = ctxScratch.strokeStyle;
        ctxDisplay.fillStyle = ctxScratch.fillStyle;

        // Draw the turtle body.
        var radius = ctxScratch.lineWidth / 2 + 10;
        ctxDisplay.beginPath();
        ctxDisplay.arc(x, y, radius, 0, 2 * Math.PI, false);
        ctxDisplay.lineWidth = 3;
        ctxDisplay.stroke();

        // Draw the turtle head.
        var WIDTH = 0.3;
        var HEAD_TIP = 10;
        var ARROW_TIP = 4;
        var BEND = 6;
        var radians = 2 * Math.PI * heading / 360;
        var tipX = x + (radius + HEAD_TIP) * Math.sin(radians);
        var tipY = y - (radius + HEAD_TIP) * Math.cos(radians);
        radians -= WIDTH;
        var leftX = x + (radius + ARROW_TIP) * Math.sin(radians);
        var leftY = y - (radius + ARROW_TIP) * Math.cos(radians);
        radians += WIDTH / 2;
        var leftControlX = x + (radius + BEND) * Math.sin(radians);
        var leftControlY = y - (radius + BEND) * Math.cos(radians);
        radians += WIDTH;
        var rightControlX = x + (radius + BEND) * Math.sin(radians);
        var rightControlY = y - (radius + BEND) * Math.cos(radians);
        radians += WIDTH / 2;
        var rightX = x + (radius + ARROW_TIP) * Math.sin(radians);
        var rightY = y - (radius + ARROW_TIP) * Math.cos(radians);
        ctxDisplay.beginPath();
        ctxDisplay.moveTo(tipX, tipY);
        ctxDisplay.lineTo(leftX, leftY);
        ctxDisplay.bezierCurveTo(leftControlX, leftControlY,
            rightControlX, rightControlY, rightX, rightY);
        ctxDisplay.closePath();
        ctxDisplay.fill();
      }
    };

    /**
     * Execute the user's code.  Heaven help us...
     */
    var execute = function(code) {
       log = [];
       ticks = 1000000;

      try {
        eval(code);
      } catch (e) {
        // Null is thrown for infinite loop.
        // Otherwise, abnormal termination is a user error.
        if (e !== Infinity) {
          alert(e);
        }
      }


      // Turtle.log now contains a transcript of all the user's actions.
      // Reset the graphic and animate the transcript.
      reset();
      pid = window.setTimeout(animate, 100);

      };

    /**
     * Iterate through the recorded path and animate the turtle's actions.
     */
    var animate = function() {
      // All tasks should be complete now.  Clean up the PID list.
      pid = 0;

      var tuple = log.shift();
      if (!tuple) {
        return;
      }
      var command = tuple.shift();
      step(command, tuple);
      display();

      // Slider returns a value between 0 and 100. Scale the speed non-linearly,
      // to give better precision at the fast end.
      var stepSpeed = 1000 * Math.pow(1 - (slider.value/100), 2);
      pid = window.setTimeout(animate, stepSpeed);
    };

    /**
     * Execute one step.
     * @param {string} command Logo-style command (e.g. 'FD' or 'RT').
     * @param {!Array} values List of arguments for the command.
     */
    var step = function(command, values) {
      switch (command) {
        case 'FD':  // Forward
          if (penDownValue) {
            ctxScratch.beginPath();
            ctxScratch.moveTo(x, y);
          }
          var distance = values[0];
          if (distance) {
            x += distance * Math.sin(2 * Math.PI * heading / 360);
            y -= distance * Math.cos(2 * Math.PI * heading / 360);
            var bump = 0;
          } else {
            // WebKit (unlike Gecko) draws nothing for a zero-length line.
            var bump = 0.1;
          }
          if (penDownValue) {
            ctxScratch.lineTo(x, y + bump);
            ctxScratch.stroke();
          }
          break;
        case 'RT':  // Right Turn
          heading += values[0];
          heading %= 360;
          if (heading < 0) {
            heading += 360;
          }
          break;
        case 'DP':  // Draw Print
          ctxScratch.save();
          ctxScratch.translate(x, y);
          ctxScratch.rotate(2 * Math.PI * (heading - 90) / 360);
          ctxScratch.fillText(values[0], 0, 0);
          ctxScratch.restore();
          break;
        case 'DF':  // Draw Font
          ctxScratch.font = values[2] + ' ' + values[1] + 'pt ' + values[0];
          break;
        case 'PU':  // Pen Up
          penDownValue = false;
          break;
        case 'PD':  // Pen Down
          penDownValue = true;
          break;
        case 'PW':  // Pen Width
          ctxScratch.lineWidth = values[0];
          break;
        case 'PC':  // Pen Colour
          ctxScratch.strokeStyle = values[0];
          ctxScratch.fillStyle = values[0];
          break;
        case 'HT':  // Hide Turtle
          visible = false;
          break;
        case 'ST':  // Show Turtle
          visible = true;
          break;
      }
    };

    // Turtle API.

    var moveForward = function(distance, id) {
      log.push(['FD', distance, id]);
    };

    var moveBackward = function(distance, id) {
      log.push(['FD', -distance, id]);
    };

    var turnRight = function(angle, id) {
      log.push(['RT', angle, id]);
    };

    var turnLeft = function(angle, id) {
      log.push(['RT', -angle, id]);
    };

    var penUp = function(id) {
      log.push(['PU', id]);
    };

    var penDown = function(id) {
      log.push(['PD', id]);
    };

    var penWidth = function(width, id) {
      log.push(['PW', Math.max(width, 0), id]);
    };

    var penColour = function(colour, id) {
      log.push(['PC', colour, id]);
    };

    var hideTurtle = function(id) {
      log.push(['HT', id]);
    };

    var showTurtle = function(id) {
      log.push(['ST', id]);
    };

    var drawPrint = function(text, id) {
      log.push(['DP', text, id]);
    };

    var drawFont = function(font, size, style, id) {
      log.push(['DF', font, size, style, id]);
    };
    this.init = init;
    this.display = display;
    this.execute = execute;
    this.moveForward = moveForward;
    this.animate = animate;
};


