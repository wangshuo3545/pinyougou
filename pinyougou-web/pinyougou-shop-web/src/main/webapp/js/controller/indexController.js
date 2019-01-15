// 定义后台首页控制器
app.controller('indexController', function ($scope, baseService) {

    // 定义方法获取登录用户名
    $scope.showLoginName = function () {
        // 发送异步请求
        baseService.sendGet("/showLoginName").then(function(response){
            // 获取响应数据
            $scope.loginName = response.data.loginName;
        });
    };
});