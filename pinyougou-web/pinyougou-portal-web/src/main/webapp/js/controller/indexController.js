/** 定义首页控制器层 */
app.controller("indexController", function($scope, baseService){

    // 根据分类id查询广告数据
    $scope.findContentByCategoryId = function (categoryId) {
        // 发送异步请求
        baseService.sendGet("/findContentByCategoryId?categoryId="
            + categoryId).then(function(response){
            // 获取响应数据 List<Content> [{},{}]
            $scope.contentList = response.data;
        });
    };

    // 跳转到搜索系统
    $scope.search = function () {
        var keywords = $scope.keywords ? $scope.keywords : "";
        location.href = "http://search.pinyougou.com?keywords=" + keywords;
    };

});