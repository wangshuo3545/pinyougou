/** 定义基础控制器 */
app.controller('baseController', function ($scope) {

    // 定义分页指令需要的配置信息对象
    $scope.paginationConf = {
        currentPage : 1, // 当前页码
        totalItems : 0, // 总记录数
        itemsPerPage : 10, // 页大小
        perPageOptions : [10,15,20,25,30], // 页码下拉列表框
        onChange : function () { // 页码发生改变，需要调用的函数(默认就会调用一次)
            $scope.reload();
        }
    };

    // 定义重新加载数据的方法
    $scope.reload = function () {
        // 分页查询品牌
        $scope.search($scope.paginationConf.currentPage,
            $scope.paginationConf.itemsPerPage);
    };


    // 定义数组记录用户选择的品牌id
    $scope.ids = [];

    // 为checkbox绑定点击事件
    $scope.updateSelection = function ($event, id) {
        // 事件对象: $event
        // 获取dom元素: $event.target
        // 判断checkbox是否选中
        if($event.target.checked){
            // checkbox选中
            // 往数组中添加元素
            $scope.ids.push(id);
        }else{
            // checkbox没有选中
            // 获取元素在数组中的索引号
            var idx = $scope.ids.indexOf(id);
            // 从数组中删除元素
            // 第一个参数：元素在数组中的索引号
            // 第二个参数：删除的个数
            $scope.ids.splice(idx, 1);

        }
    };

});