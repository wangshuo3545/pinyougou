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

    // 定义数据存储结构
    // $scope.goods.goodsDesc.itemImages;
    $scope.goods = {goodsDesc : {itemImages : []}};

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
            baseService.sendGet("/typeTemplate/findOne?id="
                        + newVal).then(function(response){
                // 获取品牌的数据 {id : '', brand_ids : "", ...}
                $scope.brandList = JSON.parse(response.data.brandIds);

                // 获取扩展属性
                $scope.goods.goodsDesc.customAttributeItems =
                    JSON.parse(response.data.customAttributeItems);
            });
        }else { // 是undefined
            $scope.brandList = [];
        }
    });












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