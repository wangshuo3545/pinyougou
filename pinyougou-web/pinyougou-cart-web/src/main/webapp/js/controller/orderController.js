app.controller('orderController', function($scope, $controller,$interval,$location, baseService){
    // 指定继承cartController
    $controller('cartController', {$scope:$scope});

    // 查询收件人地址
    $scope.findAddressByUser = function () {
        baseService.sendGet("/order/findAddressByUser").then(function(response){
            // 获取响应数据 List<Address> [{},{}]
            $scope.addressList = response.data;

            // 取默认地址
            $scope.address = $scope.addressList[0];
        });
    };

    // 地址选择的方法
    $scope.selectedAddress = function (item) {
        $scope.address = item;
    };
    // 判断地址是否选中
    $scope.isSelectedAddress = function (item) {
        return $scope.address == item;
    };

    // 定义订单对象封装请求参数
    $scope.order = {paymentType : '1'};

    // 支付选择
    $scope.selectPayType = function (payType) {
        $scope.order.paymentType = payType;
    };

    // 提交订单
    $scope.saveOrder = function () {

        // 设置收件人地址
        $scope.order.receiverAreaName = $scope.address.address;
        // 设置收件人手机
        $scope.order.receiverMobile = $scope.address.mobile;
        // 设置收件人姓名
        $scope.order.receiver = $scope.address.contact;
        // 设置订单来源(2：pc端)
        $scope.order.sourceType = "2";
        // 发送异步请求
        baseService.sendPost("/order/save", $scope.order).then(function (response) {
            // 获取响应数据
            if (response.data){
                // 判断支付方式
                if ($scope.order.paymentType == "1"){ // 微信付款
                    // 跳转到支付页面
                    location.href = "/order/pay.html";
                }else{ // 货到付款
                    // 跳转到成功页面
                    location.href = "/order/paysuccess.html";
                }
            }else {
                alert("订单保存失败！");
            }
        });
    };

    // 生成支付二维码
    $scope.genPayCode = function () {
        // 发送异步请求
        baseService.sendGet("/order/genPayCode").then(function(response){
            // 获取响应数据 {outTradeNo : '', money : 0, codeUrl: ''}
            // 1. 交易订单号
            $scope.outTradeNo = response.data.outTradeNo;
            // 2. 交易金额
            $scope.money = (response.data.totalFee / 100).toFixed(2);
            // 3. 微信支付url
            $scope.codeUrl = response.data.codeUrl;

            // 生成二维码图片
            document.getElementById("qrious").src="/barcode?url=" + $scope.codeUrl;


            /**
             * 开启定时器，间隔3秒发送异步请求检测支付状态
             * 第一个参数：回调函数
             * 第二个参数：间隔的毫秒数 3秒
             * 第三个参数：总调用次数 100
             */
            var timer = $interval(function(){
                baseService.sendGet("/order/queryPayStatus?outTradeNo="
                    + $scope.outTradeNo).then(function(response){
                    // 获取响应数据: {status : 1|2|3}
                    if (response.data.status == 1){ // 支付成功
                        // 取消定时器
                        $interval.cancel(timer);
                        // 跳转到支付成功页面
                        location.href = "/order/paysuccess.html?money=" + $scope.money;
                    }
                    if (response.data.status == 3){ // 支付失败
                        // 取消定时器
                        $interval.cancel(timer);
                        // 跳转到支付失败页面
                        location.href = "/order/payfail.html";
                    }
                });
            }, 3000, 100);

            // 总调用次数完成后，会调用then方法
            timer.then(function () {
                // 调用关闭订单接口
                // 定义提示信息
                $scope.msg = "二维码已过期，刷新页面重新获取二维码。";
            });

        });
    };

    // 获取支付金额
    $scope.getMoney = function () {
        return $location.search().money;
    };

});