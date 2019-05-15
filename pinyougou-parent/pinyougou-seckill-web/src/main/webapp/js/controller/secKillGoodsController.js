app.controller("secKillGoodsController",function ($scope,$controller,$interval,$location,secKillGoodsService) {
    $controller("baseController",{$scope:$scope});
    //1.查询商品列表
    $scope.findList =function () {
        secKillGoodsService.findList().success(function (response) {
            $scope.list = response;
        })
    }
    //2.根据id查询秒条商品详情
    $scope.showSecKillGoods=function (id) {
        location.href = "seckill-item.html#?id="+id;
    }
    //3.seckill-item.html页面一加载就调用findOne（id）在后台查询出此商品并显示
    $scope.findOne=function () {
        //3.1)从地址栏取得id参数值
         var id = $location.search()['id'];
         //3.2)根据id从后台查询出数据
        secKillGoodsService.findOne(id).success(function (response) {
            $scope.secKillGoods=response;
        })
    }

    //$interval服务基本用法演示：
    //参数1：代表要执行的函数（与setTimeout一样）
    //参数2：每隔多长时间执行一次
    //参数3：代表执行的次数
    // var i = 1;
    // var time = $interval(function () {
    //     console.log("i = " + i++);
    //     if (i == 10){
    //         $interval.cancel(time);
    //     }
    // },1000,5)

    //显示倒计时思路：将秒杀的结束时间减去当前时间，再每隔一定时间（1秒）显示一次
    //第一步：使用$interval内置服务动态获取时间并显示
    var flag = $interval(function () {
        //① 得到剩余时间(单位：秒)
        var remainTime = Math.floor( (  new Date($scope.secKillGoods.endTime).getTime()- (new Date().getTime())) /1000); //总秒数
        //② 调用处理剩余时间秒数的方法，得到一个时间字符串并赋值给全局变量
        $scope.timeStr = convertTime(remainTime);
    },1000);

    //第二步：将指定的秒数转换为如：剩余:  xxx天 10:35:20这种格式
    convertTime=function (time) {
        //2.1)处理时间
        var days = Math.floor(time / (3600 * 24));                                    //天数
        var hours = Math.floor((time - days * 3600 * 24) / 3600);                     //小时数
        var minutes = Math.floor((time - days * 3600 * 24 - hours * 3600) / 60);      //分钟数
        var seconds = time - days * 3600 * 24 - hours * 3600 - minutes * 60;            //秒数
        //2.2)处理天数
        var timeStr = "";
        if (days > 0){
            timeStr = days + "天";
        }
        timeStr += hours + ":" + minutes + ":" + seconds;
        //2.3)返回时间字符串
        return timeStr;
    }
    //后台下单
    $scope.submitOrder=function (id) {
        secKillGoodsService.submitOrder(id).success(function (response) {
            if (response.success){    //如果下单成功，就跳转到支付页面
                alert("下单成功，请在1分钟内完成支付");
                location.href = "pay.html";
            } else{
                alert(response.message);
            }
        })
    }
})
