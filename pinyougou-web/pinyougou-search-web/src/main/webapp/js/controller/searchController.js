/** 定义搜索控制器 */
app.controller("searchController" ,function ($scope, baseService) {


    // 定义搜索条件对象
    $scope.searchParam = {};

    // 商品搜索方法
    $scope.search= function () {

        // 发送异步请求
        baseService.sendPost("/Search", $scope.searchParam).then(function(response){
            // 获取响应数据 {total: 100, rows : [{},{}]}
            // js   java
            // []   List
            // {}   实体类|Map
            // [{},{}] List<Map>>
            // {key : [{},{}], key : {key : {key : [{},{key : [{},{}]}]}}}
            $scope.resultMap = response.data;
        });
    } ;

});
