/** 定义秒杀商品控制器 */
app.controller("seckillGoodsController", function($scope,$controller,$location,$timeout,baseService){

    /** 指定继承cartController */
    $controller("baseController", {$scope:$scope});

    // 查询秒杀商品
    $scope.findSeckillGoods = function () {
        baseService.sendGet("/seckill/findSeckillGoods").then(function(response){
            // 获取响应数据
            $scope.seckillGoodsList = response.data;
        });
    };

    // 根据秒杀商品id查询秒杀商品对象
    $scope.findOne = function () {
        // http://seckill.pinyougou.com/seckill-item.html?id=1
        // 获取秒杀商品id
        var id = $location.search().id;
        baseService.sendGet("/seckill/findOne?id=" + id).then(function (response) {
            // 获取响应数据 SeckillGoods {}
            $scope.entity = response.data;

            // 显示倒计时
            $scope.downCount($scope.entity.endTime);
        });
    };

    // 倒计时方法
    $scope.downCount = function (endTime) {
        // 秒杀商品的结束时间 endTime 毫秒数
        // 计算相差的毫秒数
        var millisSeconds = endTime - new Date().getTime();
        // 计算相差的秒数
        var seconds = Math.floor(millisSeconds / 1000);

        if (seconds > 0) {
            // 计算相差的分钟
            var minutes = Math.floor(seconds / 60);
            // 计算相差的小时
            var hours = Math.floor(minutes / 60);
            // 计算相差的天数
            var days = Math.floor(hours / 24);

            // 定义数组封装时间字符串
            var arr = new Array();
            if (days > 0) {
                arr.push(calc(days) + "天 ");
            }
            if (hours > 0) {
                arr.push(calc(hours - days * 24) + ":");
            }
            if (minutes > 0) {
                arr.push(calc(minutes - hours * 60) + ":");
            }
            arr.push(calc(seconds - minutes * 60));
            // 时间字符串 4天 11:22:33
            $scope.timeStr = arr.join("");

            // 开启定时器
            $timeout(function () {
                $scope.downCount(endTime);
            }, 1000);

        }else{
            $scope.timeStr = "秒杀已结束！";
        }
    };

    // 计算的方法
    var calc = function (num) {
        return num > 9 ? num : "0" + num;
    };

    // 秒杀下单
    $scope.submitOrder = function () {
        // 判断用户是否登录
        if ($scope.loginName){ // 已登录
            baseService.sendGet("/order/submitOrder?id=" + $scope.entity.id).then(function(response){
                if (response.data){
                    // 下单成功，跳转到支付页面
                    location.href = "/order/pay.html";
                }else{
                    alert("下单失败！");
                }
            });
        }else{// 未登录
            // 跳转到单点登录系统
            location.href = "http://sso.pinyougou.com/?service=" + $scope.redirectUrl;
        }
    };

});