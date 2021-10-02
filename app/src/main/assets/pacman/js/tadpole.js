'use strict';

var Tadpole = function () {
    //在代码中用到的一些常量
    const TYPE_BLOCK = -1;   //-1表示不能走的障碍
    const TYPE_PATH = 0;     //0表示可以走的通道
    const TYPE_CURRENT = 1;  //1表示角色当前位置
    const TYPE_END = 2;      //2表示角色的目的地
    const TYPE_SUCCESS = 3;  //3表示角色到达了目的地
    const TYPE_BEAN=4;       //4表示pacman可以吃的豆子

    const DIRECTION_UP = 1;    //1表示方向向上
    const DIRECTION_RIGHT = 2;  //2表示方向向右
    const DIRECTION_DOWN = 3;  //3表示方向向下
    const DIRECTION_LEFT = 4;  //4表示方向向左

    //游戏数据矩阵，初始值都为-1，在init方法中接收外界的赋值
    var gameData = [
        [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
        [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
        [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
        [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
        [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
        [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
        [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
        [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
        [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1],
        [-1, -1, -1, -1, -1, -1, -1, -1, -1, -1]
    ];

    //小蝌蚪的方向,1 表示向上，2 表示向右，3表示向下，4表示向左。默认向上
    var mDirection = 2;

    //小蝌蚪转向角度
    //var rotateAngle = 0;

    //第一次直接用insertRules添加Keyframes，之后需要先用deleteRules(7)删除已有的Keyframe（下标为7）
    //var isKeyframesFirstAdded = false;

    //是否遇到障碍物，false表示没有遇到
    var flag_block = false;

    //是否成功
    var flag_success = false;
    //所用时间,根据所用时间来进行星级评定


    var usedTime = 0;
    //游戏初始化时的时间
    var initTime = 0;
    //检查游戏是否结束时的时间
    var finishTime = 0;

    //获取屏幕的宽度和高度
    var screen_width = window.innerWidth;
    var screen_height = window.innerHeight;
    //游戏区域的尺寸
    var game_div_size = Math.min(screen_width, screen_height) - 20;
    var single_div_size = game_div_size / 10;


    //吃豆人自己吃到的全部豆子的个数
    var ateBeansNumber=0;
    //全部豆子的个数
    var allBeansNumber=0;

    //初始化游戏界面。参数data和direction分别代表游戏开始时的界面元素和小蝌蚪朝向
    var init = function (data, direction) {
        //为initTime赋值
        initTime = new Date().getTime();

        //设置mDirection
        mDirection = direction;

        //根据屏幕尺寸，动态设置游戏区域的宽和高
        var gameDivs = document.getElementById("game");
        gameDivs.style.width = game_div_size + 'px';
        gameDivs.style.height = game_div_size + 'px';
        //设置gameDiv的位置
        var top = Math.max(10, (screen_height - game_div_size) / 2);
        var left = Math.max(10, (screen_width - game_div_size) / 2);
        gameDivs.style.top = top + 'px';
        gameDivs.style.left = left + 'px';
        for (var i = 0; i < data.length; i++) {
            for (var j = 0; j < data[0].length; j++) {
                gameData[i][j] = data[i][j];


                //遍历每一关卡地图的过程中 记录下该关卡下地图中豆子的个数
                if(data[i][j]==TYPE_BEAN){
                    allBeansNumber++;
                }

                var newNode = document.createElement('div');
                newNode.className = 'line' + i;
                //设置宽和高
                newNode.style.height = single_div_size + 'px';
                newNode.style.width = single_div_size + 'px';
                //设置位置
                newNode.style.top = (i * single_div_size) + 'px';
                newNode.style.left = (j * single_div_size) + 'px';
                //设置背景图片的缩放尺寸
                newNode.style.backgroundSize = single_div_size + 'px';

                /*设置每个元素的className*/
                //是不可以走的block
                if (data[i][j] == TYPE_BLOCK) {
                    //class=block代表该元素是障碍物，class=line+i表示该元素的所在行，class=col+j表示该元素所在列（便于定位）
                    newNode.className = 'block' + ' ' + 'line' + i + ' ' + 'col' + j;
                }
                //是可以走的path
                else if (data[i][j] === TYPE_PATH) {
                    newNode.className = 'path' + ' ' + 'line' + i + ' ' + 'col' + j;
                }
                else if(data[i][j] == TYPE_BEAN) {
                    newNode.className = 'bean' + ' ' + 'line' + i + ' ' + 'col' + j;
                }
                //小蝌蚪当前所在位置current
                else if (data[i][j] == TYPE_CURRENT) {
                    //小蝌蚪的class固定为current，其背景图片根据mDirection的值动态加载
                    newNode.className = 'current' + ' ' + 'line' + i + ' ' + 'col' + j;
                    //根据角色不同的方向加载不同的背景图片
                    switch (mDirection) {
                        case DIRECTION_UP:
                            newNode.style.backgroundImage = 'url(../img/tadpole_up.png)';
                            break;
                        case DIRECTION_RIGHT:
                            newNode.style.backgroundImage = 'url(../img/tadpole_right.png)';
                            break;
                        case DIRECTION_DOWN:
                            newNode.style.backgroundImage = 'url(../img/tadpole_down.png)';
                            break;
                        case DIRECTION_LEFT:
                            newNode.style.backgroundImage = 'url(../img/tadpole_left.png)';
                            break;
                    }
                }
                //是终点end
                else if (data[i][j] == TYPE_END) {
                    newNode.className = 'end' + ' ' + 'line' + i + ' ' + 'col' + j;
                }
                //小蝌蚪成功到达终点success（通常在游戏开始时不会出现）
                else if (data[i][j] == TYPE_SUCCESS) {
                    newNode.className = 'success' + ' ' + 'line' + i + ' ' + 'col' + j;
                }

                //向gameDiv中添加当前元素
                gameDivs.appendChild(newNode);
            }
        }
        // bindKeyEnvent();
    };

    //根据当前的方向向前走，也可以表示向上下左右走（1：上，2：右，3：下，4：左）
    var move_forward = function () {
        var index_i = -1;
        var index_j = -1;
        //遍历这个数据矩阵，找到当前的位置
        for (var i = 0; i < gameData.length; i++) {
            for (var j = 0; j < gameData[0].length; j++) {
                //若当前已经到达终点，则gameData对应值为TYPE_SUCCESS，否则为TYPE_CURRENT
                if (gameData[i][j] == TYPE_CURRENT || gameData[i][j] == TYPE_SUCCESS) {
                    index_i = i;
                    index_j = j;
                    break;
                }
            }
        }
        //根据方向修改往前走了一步后的数据矩阵
        switch (mDirection) {
            case DIRECTION_UP:  //方向朝上
                //必须是面前不为block才能往前走
                if (gameData[index_i - 1][index_j] != TYPE_BLOCK) {
                    //豆子没有全部吃完
                    if (allBeansNumber!=ateBeansNumber){


                        if(gameData[index_i-1][index_j]==TYPE_BEAN){
                            //如果要到达的区域是豆子
                            ateBeansNumber++;
                            $("div.game > div").eq((index_i-1)*10+index_j).hide();//吃掉豆子
                        }

                        gameData[index_i][index_j] = TYPE_PATH;
                        gameData[index_i - 1][index_j] = TYPE_CURRENT;
                         
                        $('.current').animate({top: '-=' + single_div_size + 'px'}, "fast");//向前游动的动画
                        /*if(gameData[index_i - 1][index_j]==TYPE_BEAN){
                        	$("div.game > div")[i*10+j-10].css('background-image','none');//吃掉豆子
                   		}*/
                    }
                    //豆子吃满了
                    else{
                        gameData[index_i][index_j] = TYPE_PATH;
                        gameData[index_i - 1][index_j] = TYPE_SUCCESS;
                        $('.current').animate({top: '-=' + single_div_size / 2 + 'px'}, "fast", function(){//向前游动，到达终点的动画
                            $('.current').hide();
                            $('.end').css('background-image', 'url(../img/water.png)');
                        });
                        flag_success = true;
                    }
                }
                else
                {
                    $('.current').animate({top: '-=' + single_div_size / 2 + 'px'}, "fast");//向前游动，撞到障碍的动画
                    flag_block = true;
                }
                break;
            case DIRECTION_RIGHT:  //方向朝右
                //必须是面前不为block才能往前走
                if (gameData[index_i][index_j + 1] != TYPE_BLOCK) {
                    //豆子没有全部吃完
                    if (allBeansNumber!=ateBeansNumber){


                        if(gameData[index_i][index_j + 1]==TYPE_BEAN){
                            //如果要到达的区域是豆子
                            ateBeansNumber++;
                            $("div.game > div").eq(index_i*10+index_j+1).hide();//吃掉豆子
                        }

                        gameData[index_i][index_j] = TYPE_PATH;
                        gameData[index_i][index_j + 1] = TYPE_CURRENT;
                         
                        $('.current').animate({left: '+=' + single_div_size + 'px'}, "fast");//向前游动的动画
                    }
                    //豆子吃满了
                    else{
                        gameData[index_i][index_j] = TYPE_PATH;
                        gameData[index_i][index_j + 1] = TYPE_SUCCESS;
                        $('.current').animate({left: '+=' + single_div_size / 2 + 'px'}, "fast", function(){//向前游动，到达终点的动画
                            $('.current').hide();
                            $('.end').css('background-image', 'url(../img/water.png)');
                        });
                        flag_success = true;
                    }
                }
                else
                {
                    $('.current').animate({left: '+=' + single_div_size / 2 + 'px'}, "fast");//向前游动，撞到障碍的动画
                    flag_block = true;
                }
                break;
            case DIRECTION_DOWN:  //方向朝下
                //必须是面前不为block才能往前走
                if (gameData[index_i + 1][index_j] != TYPE_BLOCK) {
                    //豆子没有全部吃完
                    if (allBeansNumber!=ateBeansNumber){
                        if(gameData[index_i+1][index_j]==TYPE_BEAN){
                            //如果要到达的区域是豆子
                            ateBeansNumber++;
                            $("div.game > div").eq((index_i+1)*10+index_j).hide();//吃掉豆子
                        }
                        gameData[index_i][index_j] = TYPE_PATH;
                        gameData[index_i + 1][index_j] = TYPE_CURRENT;
                        $('.current').animate({top: '+=' + single_div_size + 'px'}, "fast");//向前游动的动画
                        if(gameData[index_i + 1][index_j]==TYPE_BEAN){
                        	$("div.game > div")[i*10+j+10].css('background-image','none');//吃掉豆子
                   		}
                    }
                    //豆子吃满了
                    else{
                        gameData[index_i][index_j] = TYPE_PATH;
                        gameData[index_i + 1][index_j] = TYPE_SUCCESS;
                        $('.current').animate({top: '+=' + single_div_size / 2 + 'px'}, "fast", function(){//向前游动，到达终点的动画
                            $('.end').css('background-image', 'url(../img/water.png)');
                        });
                        flag_success = true;
                    }
                }
                else
                {
                    $('.current').animate({top: '+=' + single_div_size / 2 + 'px'}, "fast");//向前游动，撞到障碍的动画
                    flag_block = true;
                }
                break;
            case DIRECTION_LEFT:  //方向朝左
                //必须是面前不为block才能往前走
                if (gameData[index_i][index_j - 1] != TYPE_BLOCK) {
                    //豆子没有全部吃完
                    if (allBeansNumber!=ateBeansNumber){


                        if(gameData[index_i][index_j - 1]==TYPE_BEAN){
                            //如果要到达的区域是豆子
                            ateBeansNumber++;
                            $("div.game > div").eq(index_i*10+index_j-1).hide();//吃掉豆子
                        }

                        gameData[index_i][index_j] = TYPE_PATH;
                        gameData[index_i][index_j - 1] = TYPE_CURRENT;
                         
                        $('.current').animate({left: '-=' + single_div_size + 'px'}, "fast");//向前游动的动画
                        if(gameData[index_i][index_j - 1]==TYPE_BEAN){
                        	$("div.game > div")[i*10+j-1].css('background-image','none');//吃掉豆子
                   		}
                    }
                    //豆子吃满了
                    else{
                        gameData[index_i][index_j] = TYPE_PATH;
                        gameData[index_i][index_j - 1] = TYPE_SUCCESS;
                        $('.current').animate({left: '-=' + single_div_size / 2 + 'px'}, "fast", function(){//向前游动，到达终点的动画
                            $('.current').hide();
                            $('.end').css('background-image', 'url(../img/water.png)');
                        });
                        flag_success = true;
                    }
                }
                else
                {
                     
                    $('.current').animate({left: '-=' + single_div_size / 2 + 'px'}, "fast");//向前游动，撞到障碍的动画
                    flag_block = true;
                }
                break;
        }
    };
    //角色左转
    var turn_left = function () {
        switch (mDirection){
            case DIRECTION_UP:
                mDirection = DIRECTION_LEFT;
                $('.current').css('background-image', 'url(../img/tadpole_left.png)');
                break;
            case DIRECTION_RIGHT:
                mDirection = DIRECTION_UP;
                $('.current').css('background-image', 'url(../img/tadpole_up.png)');
                /*
                var angle = 90;
                //每100毫秒（0.1秒）刷新一帧
                var interval = setInterval(function () {
                    angle -= 3;
                    if (angle != 0){
                        $('.current').css('background-image', 'url(../img/tadpole_' + angle + '.png)');
                    }
                    else{
                        $('.current').css('background-image', 'url(../img/tadpole_up.png)');
                        clearInterval(interval);
                    }
                }, 33);
                */
                break;
            case DIRECTION_DOWN:
                mDirection = DIRECTION_RIGHT;
                $('.current').css('background-image', 'url(../img/tadpole_right.png)');
                break;
            case DIRECTION_LEFT:
                mDirection = DIRECTION_DOWN;
                $('.current').css('background-image', 'url(../img/tadpole_down.png)');
                break;
        }

        /*
        //左转动画效果，从(rotateAngle)deg转到(rotateAngle-90)deg
        var rotateKeyframe = '@-webkit-keyframes rotateTadpole{\n\t0% {-webkit-transform: rotate(' + rotateAngle + 'deg);}\n\t100% {-webkit-transform: rotate(' + (rotateAngle - 90) + 'deg);}\n}';
        var style = document.styleSheets[0];    //style是一个CSSStyleSheet对象，代表引入的第一条CSS样式表
        try {
            //第一次添加Keyframes时，不存在已有的Keyframes，故直接添加
            if (isKeyframesFirstAdded == false){
                isKeyframesFirstAdded = true;
            }
            else{   //最后一条下标为7，是动态添加的旋转keyframes。需要先删除已有的Keyframes，再添加新的
                style.deleteRule(7);
            }
            style.insertRule(rotateKeyframe, 7);
            //    动画keyframes属性：
            //    名称：rotateTadpole
            //    持续时间：1s
            //    播放结束后停留在最后一帧：forwards

            $('.current').css('-webkit-animation', 'rotateTadpole 1s forwards');
        }
        catch (e){
            //异常原因是无法获取CSS样式表
            alert('程序运行出现异常，请重新进行游戏');
        }
        rotateAngle -= 90;
        */
    };
    //角色右转
    var turn_right = function () {
        switch (mDirection){
            case DIRECTION_UP:
                mDirection = DIRECTION_RIGHT;
                $('.current').css('background-image', 'url(../img/tadpole_right.png)');
                break;
            case DIRECTION_RIGHT:
                mDirection = DIRECTION_DOWN;
                $('.current').css('background-image', 'url(../img/tadpole_down.png)');
                break;
            case DIRECTION_DOWN:
                mDirection = DIRECTION_LEFT;
                $('.current').css('background-image', 'url(../img/tadpole_left.png)');
                break;
            case DIRECTION_LEFT:
                mDirection = DIRECTION_UP;
                $('.current').css('background-image', 'url(../img/tadpole_up.png)');
                break;
        }
        /*
        //右转动画效果，从(rotateAngle)deg转到(rotateAngle+90)deg
        var rotateKeyframe = '@-webkit-keyframes rotateTadpole{\n\t0% {-webkit-transform: rotate(' + rotateAngle + 'deg);}\n\t100% {-webkit-transform: rotate(' + (rotateAngle + 90) + 'deg);}\n}';
        var style = document.styleSheets[0];    //style是一个CSSStyleSheet对象，代表引入的第一条CSS样式表
        try {
            //第一次添加Keyframes时，不存在已有的Keyframes，故直接添加
            if (isKeyframesFirstAdded == false){
                isKeyframesFirstAdded = true;
            }
            else{   //最后一条下标为7，是动态添加的旋转keyframes。需要先删除已有的Keyframes，再添加新的
                style.deleteRule(7);
            }
            style.insertRule(rotateKeyframe, 7);
            //    动画keyframes属性：
            //    名称：rotateTadpole
            //    持续时间：1s
            //    播放结束后停留在最后一帧：forwards

            $('.current').css('-webkit-animation', 'rotateTadpole 1s forwards');
        }
        catch (e){
            //异常原因是无法获取CSS样式表
            alert('程序运行出现异常，请重新进行游戏');
        }
        rotateAngle += 90;
        */
    };

    //绑定键盘事件
    // var bindKeyEnvent = function () {
    //     document.onkeydown = function (ev) {
    //         if (ev.keyCode == 38) {           //上移键
    //             move_forward();
    //         } else if (ev.keyCode == 39) {    //右移键
    //             // move_forward(2);
    //             turn_right();
    //         } else if (ev.keyCode == 40) {    //下移键
    //             // move_forward(3);
    //         } else if (ev.keyCode == 37) {    //左移键
    //             // move_forward(4);
    //             turn_left();
    //         }
    //     }
    // };

    //判断游戏结果
        var check_game = function (data) {
            if(flag_block == true)
            {
                flag_block = false;
                var rating = 0;
                window.android.showGameResult(rating, "碰撞到障碍物，没有到达目的地");
            }
            else
            {
                //用户的评星
                var rating = 0;
                for (var i = 0; i < data.length; i++) {
                    for (var j = 0; j < data[0].length; j++) {
                        if (allBeansNumber==ateBeansNumber) {
                            flag_success = true;
                            break;
                        }
                    }
                }
                if (flag_success == true) {
                    //如果成功到达目的地并且用时少于1分钟，评为3星
                    if(usedTime < 30000){
                        rating = 3;
                    }
                    //如果成功到达目的地并且用时少于3分钟，评为2星
                    else if(usedTime < 180000){
                        rating = 2;
                    }
                    //如果成功到达目的地并且用时少于5分钟，评为1星
                    else if(usedTime < 300000){
                        rating = 1;
                    }
                    window.android.showGameResult(rating, "恭喜你成功了");
                    // alert("恭喜你成功了,用时"+usedTime+"评星："+rating);
                }else{
                    rating=-2;
                    window.android.showGameResult(rating, "没有到达目的地");
                    // alert("没有到达目的地,用时"+usedTime+"评星："+rating);
                }
            }
        };

    //执行小蝌蚪游动和转向的方法。参数code为生成代码，需要经过处理后方可执行。
        //目前暂不能实现旋转的动画效果
        var execute = function (code) {
            try {
                //按下运行按钮时，计时停止，计算用时
                finishTime = new Date().getTime();
                usedTime = finishTime - initTime;
                //实际执行时的代码
                var execute_code = new Array();

                //按换行符"\n"分解code
                var arr_code = code.split("\n");

                var line = 0;
                while (line < arr_code.length) {   //line是当前读取的行号
                    //若包含for循环，则根据for循环语法结构，计算循环次数，而后将复制循环体内的语句。这样就可以将循环结构转变为顺序结构
                    if (arr_code[line].indexOf("for(") != -1) {
                        var i_begin = 0;
                        var i_end = arr_code[line].substring(arr_code[line].indexOf("<") + 2, arr_code[line].indexOf("; i++"));//获取计数器上限
                        var loop_times = i_end - i_begin;

                        //从当前for循环所在行开始，找到当前for循环的结束标记
                        var end_line = line;
                        while (arr_code[end_line] != "}") {
                            end_line++;
                        }

                        //去掉第一行和最后一行，即为循环体
                        var loop_body = new Array();
                        for (var i = line + 1; i < end_line; i++) {
                            loop_body[i - line - 1] = arr_code[i];//loop_body是从下标0开始
                        }

                        //根据循环体和循环次数，将原本的for循环结构，转变为顺序结构
                        for (var i = 0; i < loop_times * loop_body.length; i++) {
                            execute_code.push(loop_body[i % loop_body.length]);
                        }
                        //从当前for循环结束的下一行开始，继续遍历
                        line = end_line + 1;
                    }
                    //for当前循环结束标记
                    else if (arr_code[line] == "}" || arr_code[line] == "\n") {
                        line++;
                    }
                    //不包含在for循环中的代码，即只执行一次的代码
                    else {
                        execute_code.push(arr_code[line]);
                        line++;
                    }
                }

                var timesRun = 0;
                //每500毫秒（0.5秒）执行一条命令
                var interval = setInterval(function () {
                    //如果执行完所有的代码，则停止定时器，并检查是否到达目的地
                    if (timesRun == execute_code.length - 1) {
                        clearInterval(interval);
                        check_game(gameData);
                    }
                    //执行代码。但是，一旦撞到障碍物，或者到达终点，则立即检查游戏结果，不再执行后续代码
                    else {
                        if (flag_block == true || flag_success == true) {
                            clearInterval(interval);
                            check_game(gameData);
                        }
                        else {
                            eval(execute_code[timesRun]);
                            timesRun++;
                        }
                    }
                }, 1000);
            }
            catch (e) {
                if (e !== Infinity) {
                    alert(e);
                }
            }

        };

    //--创建触摸监听，当浏览器打开页面时，触摸屏幕触发事件，进行音频播放
    document.addEventListener('touchstart', function () {
        function audioAutoPlay() {
            var audio = document.getElementById('bgm_global');
                audio.play();
        }
        audioAutoPlay();
    });
    /*
    function checkboxOnclick(checkbox) {
        if (checkbox.checked){
            alert("checked");
            $('#bgm_global').play();
        }
        else{
            alert("not checked");
            $('#bgm_global').pause;
        }
    });
    */
    //导出API
    this.init = init;
    this.move_forward = move_forward;
    this.turn_left = turn_left;
    this.turn_right = turn_right;
    this.execute = execute;
}
