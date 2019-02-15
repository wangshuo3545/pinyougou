app.controller('cartController', function($scope, $controller, baseService){
    // 指定继承baseController
    $controller('baseController', {$scope:$scope});

    // 获取购物车
    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart").then(function(response){
            // [{},{}]
            $scope.carts = response.data;

            // 定义json对象封装购买总数与总金额
            $scope.totalEntity = {totalItem : 0, totalNum : 0, totalMoney : 0};

            // 购买总数与总金额
            for (var i = 0; i < $scope.carts.length; i++){
                // 获取数组元素 Cart对象
                var cart = $scope.carts[i];

                // 统计买的商品种类
                $scope.totalEntity.totalItem += cart.orderItems.length;

                for (var j = 0; j < cart.orderItems.length; j++){
                    // 获取数组元素
                    var orderItem = cart.orderItems[j];

                    // 统计购买的总件数
                    $scope.totalEntity.totalNum += orderItem.num;
                    // 统计购买的总金额
                    $scope.totalEntity.totalMoney += orderItem.totalFee;
                }
            }
        });
    };

    // 添加商品到购物车(加减、删除)
    $scope.addCart = function (itemId, num) {
        baseService.sendGet("/cart/addCart?itemId="
            + itemId + "&num=" + num).then(function(response){
            if (response.data){
                // 加入购物车成功，重新查询购物车
                $scope.findCart();
            }else {
                alert("操作失败！");
            }
        });
    };
});