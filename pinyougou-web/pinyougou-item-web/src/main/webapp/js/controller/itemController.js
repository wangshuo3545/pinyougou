// 商品详情控制器
app.controller('itemController', function ($scope) {

    // 购买数量加减操作
    $scope.addNum = function (x) {
        // ng-model 绑定的数据都是字符串
        $scope.num = parseInt($scope.num);

        $scope.num += x;
        // 判断购买数量
        if ($scope.num < 1){
            $scope.num = 1;
        }
    };

    // 定义json对象保存用户选中的规格选项
    $scope.spec = {};

    // 记录用户选择的规格选项
    $scope.selectedSpec = function (specName, optionName) {
        $scope.spec[specName] = optionName;
        // 根据用户选择的规格选项到 itemList SKU数组中 查询对应的 SKU
        searchSku();
    };

    // 判断规格选项是否选中
    $scope.isSelected = function (specName, optionName) {
        return $scope.spec[specName] == optionName;
    };


    // 加载默认的SKU
    $scope.loadSku = function () {
        // 获取默认的SKU
        $scope.sku = itemList[0];
        // 规格选项
        $scope.spec = JSON.parse($scope.sku.spec);
    };

    // 根据用户选择的规格选项到 itemList SKU数组中 查询对应的 SKU
    var searchSku = function () {
        // itemList : [{},{}]
        // 迭代SKU数组
        for (var i =  0; i < itemList.length; i++){
            // 取一个SKU (tb_item)表中的一行数据
            var sku = itemList[i];
            // 判断SKU
            if (sku.spec == JSON.stringify( $scope.spec )){
                $scope.sku = sku;
                break;
            }
        }
    };

    // 加入购物车事件绑定
    $scope.addToCart = function () {
        alert("sku的id: " + $scope.sku.id + "，数量：" + $scope.num);
    };
});