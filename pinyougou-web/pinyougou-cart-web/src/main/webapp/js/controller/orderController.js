app.controller('orderController', function($scope, $controller, baseService){
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

});