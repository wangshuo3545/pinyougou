/** 定义搜索控制器 */
app.controller("searchController" ,function ($scope, $sce, $location, baseService) {


    // 定义搜索条件对象
    $scope.searchParam = {keywords : '', category : '',
        brand : '', price : '', spec : {}, page : 1, rows : 10,
        sortField : '', sortValue : ''};

    // 商品搜索方法
    $scope.search = function () {

        // 发送异步请求
        baseService.sendPost("/Search", $scope.searchParam).then(function(response){
            // 获取响应数据 {total: 100, rows : [{},{}]}
            // js   java
            // []   List
            // {}   实体类|Map
            // [{},{}] List<Map>>
            // {key : [{},{}], key : {key : {key : [{},{key : [{},{}]}]}}}
            $scope.resultMap = response.data;
            // 定义显示关键字的变量
            $scope.keyword =  $scope.searchParam.keywords;

            // 生成页码
            $scope.initPages();
        });
    };

    // 把html格式字符串转化成html标签
    $scope.trustHtml = function (html) {
        return $sce.trustAsHtml(html);
    };

    // 封装过滤条件的方法
    $scope.addSearchItem = function (key, value) {
        // 判断key如果分类、品牌、价格
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchParam[key] = value;
        }else {
            // 规格选项
            $scope.searchParam.spec[key] = value;
        }
        // 执行搜索
        $scope.search();
    };

    // 删除过滤条件的方法
    $scope.removeSearchItem = function (key) {
        // 判断key如果分类、品牌、价格
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchParam[key] = '';
        }else {
            // 规格选项
            delete $scope.searchParam.spec[key];
        }

        // 执行搜索
        $scope.search();
    };

    // 生成页码的方法
    $scope.initPages = function () {
        // 定义页码数组
        $scope.pageNums = [];
        // 定义开始页码
        var firstPage = 1;
        // 定义结束页码
        var lastPage = $scope.resultMap.totalPages;

        // 控制省略号
        $scope.firstDot = true;
        $scope.lastDot = true;

        // 判断总页数是不是大于5
        if ($scope.resultMap.totalPages > 5){
            // 判断当前页码是否靠首页近些
            if ($scope.searchParam.page <= 4){
                $scope.firstDot = false;
                lastPage = 5; // 结束页码
            }else if ($scope.searchParam.page >= $scope.resultMap.totalPages - 3){
                // 判断当前页码是否靠尾页近些 6 10
                firstPage = $scope.resultMap.totalPages - 4; // 开始页码
                $scope.lastDot = false;
            }else{
                // 当前页码在中间位置
                firstPage = $scope.searchParam.page - 2;
                lastPage = $scope.searchParam.page + 2;
            }
        }else{
            $scope.firstDot = false;
            $scope.lastDot = false;
        }

        // 循环产生页码
        for (var i = firstPage; i <= lastPage; i++){
            $scope.pageNums.push(i);
        }
    };

    // 根据页码搜索
    $scope.pageSearch = function (page) {
        //alert(typeof page);
        page = parseInt(page);
        if (page > $scope.resultMap.totalPages){
            page = $scope.resultMap.totalPages;
        }
        if (page < 1){
            page = 1;
        }
        // 判断页码的有效性
        if (page >= 1 && page != $scope.searchParam.page
                && page <= $scope.resultMap.totalPages){

            $scope.searchParam.page = page;
            // 执行搜索
            $scope.search();
        }
        $scope.jumpPage = page;
    };

    // 根据排序搜索
    $scope.sortSearch = function (key, value) {
        $scope.searchParam.sortField = key;
        $scope.searchParam.sortValue = value;
        // 执行搜索
        $scope.search();
    };

    // 定义获取请求参数的方法
    $scope.getKeywords = function () {
        // http://search.pinyougou.com/?keywords=小米
        //var a = window.decodeURIComponent(location.search.split("=")[1]);
        // ?keywords=小米&name=admin 把参数转化成json对象
        var json = $location.search();
        $scope.searchParam.keywords = json.keywords;
        // 执行搜索
        $scope.search();
    };


});
