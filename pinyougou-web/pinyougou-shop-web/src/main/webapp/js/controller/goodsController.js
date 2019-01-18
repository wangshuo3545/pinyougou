/** 定义控制器层 */
app.controller('goodsController', function($scope, $controller, baseService){

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    /** 添加或修改 */
    $scope.saveOrUpdate = function(){

        // 获取富文本编辑器中的内容
        $scope.goods.goodsDesc.introduction = editor.html();

        /** 发送post请求 */
        baseService.sendPost("/goods/save", $scope.goods)
            .then(function(response){
                if (response.data){
                    /** 清空数据 */
                    $scope.goods = {};
                    /** 清空富文本编辑器中的内容 */
                    editor.html("");
                }else{
                    alert("操作失败！");
                }
            });
    };
    // 定义图片上传的方法
    $scope.upload = function () {
        // 调用服务层方法
        baseService.uploadFile().then(function (response) {
            // 获取响应数据 {status : 200, url : ''}
            if (response.data.status == 200){
                // {"color":"金色","url":"http://image.pinyougou.com/jd/wKgMg1qtKEOATL9nAAFti6upbx4132.jpg"}
                $scope.picEntity.url = response.data.url;
            }else {
                alert("上传失败！");
            }
        });
    };

    // 定义商品数据存储结构 json对象
    // $scope.goods.goodsDesc.itemImages;
    $scope.goods = {goodsDesc : {itemImages : [], specificationItems : []}};

    // 添加上传的图片
    $scope.addPic = function () {
        $scope.goods.goodsDesc.itemImages.push($scope.picEntity);
    };
    // 删除上传的图片
    $scope.removePic = function (idx) {
        $scope.goods.goodsDesc.itemImages.splice(idx, 1);
    };

    // 根据父级id查询商品分类
    $scope.findItemCatByParentId = function (parentId, name) {
        baseService.sendGet("/itemCat/findItemCatByParentId?parentId="
            + parentId).then(function(response){
            // 获取响应数据
            $scope[name] = response.data;
        });
    };

    // $scope.$watch(): 它可以监控$scope里面的变量改变，如果改变就会调用一个函数
    // $scope.$watch() 监控goods.category1Id变量发生改变，查询商品二级分类
    $scope.$watch("goods.category1Id", function (newVal, oldVal) {
        //alert("新值：" + newVal + ", 旧值: " + oldVal);
        if (newVal){ // 不是undefined
            $scope.findItemCatByParentId(newVal, "itemCatList2");
        }else { // 是undefined
            $scope.itemCatList2 = [];
        }
    });

    // $scope.$watch() 监控goods.category2Id变量发生改变，查询商品三级分类
    $scope.$watch("goods.category2Id", function (newVal, oldVal) {
        //alert("新值：" + newVal + ", 旧值: " + oldVal);
        if (newVal){ // 不是undefined
            $scope.findItemCatByParentId(newVal, "itemCatList3");
        }else { // 是undefined
            $scope.itemCatList3 = [];
        }
    });

    // $scope.$watch() 监控goods.category3Id变量发生改变，查找类型模板id
    $scope.$watch("goods.category3Id", function (newVal, oldVal) {
        if (newVal){ // 不是undefined
            // 迭代三级分类数组[{},{}]
            for (var i = 0; i < $scope.itemCatList3.length; i++){
                // 取一个数组元素 {}
                var itemCat = $scope.itemCatList3[i];
                if (itemCat.id == newVal){
                    // 取类型模板id
                    $scope.goods.typeTemplateId = itemCat.typeId;
                    break;
                }
            }
        }else { // 是undefined
            $scope.goods.typeTemplateId = null;
        }
    });

    // $scope.$watch() 监控goods.typeTemplateId变量发生改变，查询类型模板对象
    $scope.$watch("goods.typeTemplateId", function (newVal, oldVal) {
        if (newVal){ // 不是undefined

            //1. 查询类型模板对象
            baseService.sendGet("/typeTemplate/findOne?id="
                        + newVal).then(function(response){
                // 获取品牌的数据 {id : '', brand_ids : "", ...}
                $scope.brandList = JSON.parse(response.data.brandIds);

                // 获取扩展属性
                $scope.goods.goodsDesc.customAttributeItems =
                    JSON.parse(response.data.customAttributeItems);
            });

            // 2. 查询规格选项数据
            baseService.sendGet("/typeTemplate/findSpecByTypeTemplateId?id="
                + newVal).then(function(response){
                // 获取响应数据
                /**
                 * [{"id":27,"text":"网络", "options" : [{id : '',optionName:'',..},{}]},
                    {"id":32,"text":"机身内存", "options" : [{},{}]}]
                 */
                $scope.specList = response.data;
            });


        }else { // 是undefined
            $scope.brandList = [];
        }
    });

    // 记录用户选中的规格选项
    $scope.updateSpecAttr = function ($event, specName, optionName) {
        /**
         * $scope.goods.goodsDesc.specificationItems = [];
          [{"attributeValue":["联通4G","移动4G","电信4G"],"attributeName":"网络"},
           {"attributeValue":["64G","128G"],"attributeName":"机身内存"}]
         */
        // obj : {"attributeValue":["联通4G","移动4G","电信4G"],"attributeName":"网络"}
        var obj = searchJsonByKey($scope.goods.goodsDesc.specificationItems, "attributeName", specName);
        if (obj){ // 不是null
            // 判断checkbox是否选中
            if ($event.target.checked) {
                obj.attributeValue.push(optionName);
            }else{
                // 得到optionName在obj.attributeValue中的索引号
                var idx = obj.attributeValue.indexOf(optionName);
                obj.attributeValue.splice(idx, 1);
                // 判断obj.attributeValue数组长度是否等于0
                if (obj.attributeValue.length == 0){
                    // {"attributeValue":[],"attributeName":"机身内存"} 在 specificationItems中的索引号
                    idx = $scope.goods.goodsDesc.specificationItems.indexOf(obj);
                    // 从规格数组中删除 一个规格选项对象
                    $scope.goods.goodsDesc.specificationItems.splice(idx,1);
                }
            }
        }else{
            $scope.goods.goodsDesc.specificationItems
                .push({attributeValue:[optionName],attributeName:specName});
        }
    };

    // 根据指定的key从json数组中查询一个json对象
    var searchJsonByKey = function (jsonArr, key, value) {
        /**
         * jsonArr:
         * [{"attributeValue":["联通4G","移动4G","电信4G"],"attributeName":"网络"},
         {"attributeValue":["64G","128G"],"attributeName":"机身内存"}]
         */
        for (var i = 0; i < jsonArr.length; i++){
            // 取一个数组元素
            // {"attributeValue":["联通4G","移动4G","电信4G"],"attributeName":"网络"}
            var obj = jsonArr[i];
            if (obj[key] == value){
                return obj;
            }
        }
        return null;
    };

    // 创建SKU商品数组
    $scope.createItems = function () {
        // 定义SKU商品数组，并且初始化 spec: {"网络":"联通4G"}
        $scope.goods.items = [{spec : {}, price : 0, num : 9999, status : '0', isDefault : '0' }];

        // 获取用户选中的规格选项
        // [{"attributeValue":["联通4G","电信3G"],"attributeName":"网络"}]
        /**
         *  [{"attributeValue":["联通4G","移动4G","电信4G"],"attributeName":"网络"}]
         */
        var specItems = $scope.goods.goodsDesc.specificationItems;
        // 迭代规格选项数组
        for (var i = 0; i < specItems.length; i++){
            // 取一个数组元素
            // {"attributeValue":["联通4G","移动4G","电信4G"],"attributeName":"网络"}
            var obj = specItems[i];
            // 把用户选中的规格选项转化成SKU商品
            $scope.goods.items  = swapItems( $scope.goods.items, obj.attributeValue, obj.attributeName);
        }
    };

    // 把用户选中的规格选项转化成SKU商品，返回一个新的SKU商品数组
    var swapItems = function (items, attributeValue, attributeName) {

        // 定义一个新的SKU商品数组
        var newItems = [];
        // items: [{spec : {}, price : 0, num : 9999, status : '0', isDefault : '0' }]
        // 迭代原来的SKU商品数组
        for (var i = 0; i < items.length; i++){
            // {spec : {}, price : 0, num : 9999, status : '0', isDefault : '0' }
            var item = items[i];

            // "attributeValue":["联通4G","移动4G","电信4G"]
            for (var j = 0; j < attributeValue.length; j++){
                // 克隆item，产生新的
                var newItem = JSON.parse(JSON.stringify(item));
                // 设置规格 spec: {"网络":"联通4G"}
                newItem.spec[attributeName] = attributeValue[j];
                // 添加新的SKU商品
                newItems.push(newItem);
            }
        }
        return newItems;
    };

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function(page, rows){
        baseService.findByPage("/goods/findByPage", page,
			rows, $scope.searchEntity)
            .then(function(response){
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    // 定义状态码提示文本数组
    $scope.status = ['未审核','已审核','审核不通过','已关闭'];











    /** 显示修改 */
    $scope.show = function(entity){
       /** 把json对象转化成一个新的json对象 */
       $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function(){
        if ($scope.ids.length > 0){
            baseService.deleteById("/goods/delete", $scope.ids)
                .then(function(response){
                    if (response.data){
                        /** 重新加载数据 */
                        $scope.reload();
                    }else{
                        alert("删除失败！");
                    }
                });
        }else{
            alert("请选择要删除的记录！");
        }
    };
});