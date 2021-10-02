'use strict';


var Tadpole = function () {

//    //判断游戏结果
//            var check_game = function (data) {
//                if(flag_block == true)
//                {
//                    flag_block = false;
//                    var rating = 0;
//                    window.android.showGameResult(rating, "经过障碍物，没有到达目的地");
//                }
//                else
//                {
//                    //用户的评星
//                    var rating = 0;
//                    for (var i = 0; i < data.length; i++) {
//                        for (var j = 0; j < data[0].length; j++) {
//                            if (data[i][j] == 3) {
//                                flag_success = true;
//                                break;
//                            }
//                        }
//                    }
//                    if (flag_success == true) {
//                        //如果成功到达目的地并且用时少于1分钟，评为3星
//                        if(usedTime < 30000){
//                            rating = 3;
//                        }
//                        //如果成功到达目的地并且用时少于3分钟，评为2星
//                        else if(usedTime < 180000){
//                            rating = 2;
//                        }
//                        //如果成功到达目的地并且用时少于5分钟，评为1星
//                        else if(usedTime < 300000){
//                            rating = 1;
//                        }
//                        window.android.showGameResult(rating, "恭喜你成功了");
//                        // alert("恭喜你成功了,用时"+usedTime+"评星："+rating);
//                    }else{
//                        rating=-2;
//                        window.android.showGameResult(rating, "没有到达目的地");
//                        // alert("没有到达目的地,用时"+usedTime+"评星："+rating);
//                    }
//                }
//            };

    //执行小蝌蚪游动和转向的方法。参数code为生成代码，需要经过处理后方可执行。
        //目前暂不能实现旋转的动画效果
        var execute = function (code) {
            eval(code);
            window.android.showGameResult(0, "第三模块测试");
        };

    //导出API
    this.execute = execute;
}
var tp = new Tadpole();
