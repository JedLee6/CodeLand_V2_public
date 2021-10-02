//角色向前走
Blockly.JavaScript['tp_move_forward'] = function(block) {
    return 'move_forward();\n';
};
//角色向左走
Blockly.JavaScript['tp_move_left'] = function(block) {
    return 'move_left();\n';
};
//角色向右走
Blockly.JavaScript['tp_move_right'] = function(block) {
    return 'move_right();\n';
};
//角色向左走
Blockly.JavaScript['tp_move_down'] = function(block) {
    return 'move_down();\n';
};
//角色左转
Blockly.JavaScript['tp_turn_left'] = function(block) {
    return 'turn_left();\n';
};
//角色右转
Blockly.JavaScript['tp_turn_right'] = function(block) {
    return 'turn_right();\n';
};
//重复执行n次
Blockly.JavaScript['tp_repeat'] = function(block) {
    var strTimes = block.getFieldValue("TIMES");
    var times = 0;
    //进行字符串到数字的转化
    times = parseInt(strTimes);
    if(isNaN(times)){
        times = 0;
    }
    var contentStatement = Blockly.JavaScript.statementToCode(block, 'CONTENT').substring(2);
    var code = 'for(var i = 0; i < '+times+'; i++){\n\t'+contentStatement+'}\n';
    return code;
};
