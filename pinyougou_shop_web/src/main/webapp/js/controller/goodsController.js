 //控制层 
app.controller('goodsController' ,function($scope,$controller ,goodsService,itemCatService,typeTemplateService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}  ;
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	};

	$scope.entity = {tbGoods:{}, tbGoodsDesc:{},tbItemList:[]};

	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象

		if($scope.entity.tbGoods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			//添加设置富文本编辑框的html标签
			$scope.entity.tbGoodsDesc.introduction = editor.html();
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//添加成功后，清空录入商品的数据
					$scope.entity = {};
					//清空富文本编辑框html标签内容
					editor.html("");

				}else{
					//添加失败，展示错误提示信息
					alert(response.message);
				}
			}		
		);				
	};
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}						
			}		
		);				
	};
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	//将查询到的当前页数据列表赋给list集合
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};

	//读取一级分类
	$scope.selectItemCat1List = function () {
		itemCatService.findByParentId(0).success(function (response) {
			$scope.itemCat1List = response;
        })
    };

	//读取二级分类 $watch:相当于监控器，用于监控一级分类下拉选内容的变化
	//有两个参数，参数一：监控的变量值(一级分类的id) 参数二：监控的内容发生变化，需要做的事情（即函数）
	//newValue:监控的变量变化后的值  oldValue：监控的变量变化前的值
	$scope.$watch("entity.tbGoods.category1Id",function (newValue,oldValue) {
		itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat2List = response;
        })
    });

	//读取三级分类
    $scope.$watch("entity.tbGoods.category2Id",function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat3List = response;
        })
    });

    //读取模板id值
	$scope.$watch("entity.tbGoods.category3Id",function (newValue, oldValue) {
		itemCatService.findOne(newValue).success(function (response) {
			$scope.entity.tbGoods.typeTemplateId = response.typeId;
        })
    });

	//读取模板关联的品牌,关联的扩展属性
	$scope.$watch("entity.tbGoods.typeTemplateId",function (newValue, oldValue) {
		typeTemplateService.findOne(newValue).success(function (response) {
			//从数据库中查询到的品牌列表数据是json字符串
			//将模板关联的品牌json字符串转换位json数组对象
			$scope.brandList = JSON.parse(response.brandIds);

			//将模板关联的扩展属性json字符串转换位json数组对象
			$scope.entity.tbGoodsDesc.customAttributeItems= JSON.parse(response.customAttributeItems);

        });

        //根据模板id查询模板关联的规格及规格选项列表 模板关联的规格列表json字符串：[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
		//$scope.specList = [{"id":27,"text":"网络",options:['移动3G','联通4G']}];
        typeTemplateService.findSpecList(newValue).success(function (response) {
			$scope.specList = response;
        })
    });

	//初始化图片json格式对象
	$scope.image_entity = {};
	//图片上传
	$scope.uploadFile = function () {
		uploadService.uploadFile().success(function (response) {
			if(response.success){
				//图片上传成功，提取文件上传路径url
				$scope.image_entity.url = response.message;
			}else{
				//图片上传失败，弹出框展示提示信息
				alert(response.message);
			}
        })
    };

	//初始化组合实体json格式对象
	$scope.entity = {tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[]}};

    //保存图片列表
	$scope.add_image_entity = function () {
		$scope.entity.tbGoodsDesc.itemImages.push($scope.image_entity);
    };

    //移除图片列表中的图片
	$scope.remove_image_entity = function (index) {
		$scope.entity.tbGoodsDesc.itemImages.splice(index,1);
    };

	//初始化勾选的规格列表
	//$scope.entity = {tbGoods:{},tbGoodsDesc:{itemImages:[],specificationItems:[]}};
    //分析勾选的规格列表：
    /*[
	 {"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},
	 {"attributeName":"屏幕尺寸","attributeValue":["6寸","5寸"]}
	 ]*/
	//勾选规格选项与取消勾选规格选项
	//$event:内置的复选框对象，specName:规格名称，optionName:规格选项名称

	$scope.updateSpecAttribute = function ($event, speciName, specOptionName) {

		//获取规格结果对象 (勾选的规格对象)
		var object = $scope.searchObjectByKey($scope.entity.tbGoodsDesc.specificationItems,
			'attributeName',speciName);

		//判断规格结果对象是否为空，就是判断规格名称是否存在于勾选的规格列表中
		if(object != null){
			//不为空，说明勾选的规格列表中存在该规格名称
			//判断是勾选规格选项还是取消勾选规格选项
			if($event.target.checked){
				//勾选状态，向规格对象的规格选项数组中添加规格
				object.attributeValue.push(specOptionName);
			}else{
				//取消勾选，从规格对象的规格选项数组中移除规格
				//获取该规格选项在数组中的索引
				var index = object.attributeValue.indexOf(specOptionName);
				object.attributeValue.splice(index,1);

				//如果勾选的规格选项都取消了，就从勾选的规格列表中移除整个规格对象
				if(object.attributeValue.length == 0){
					//获取规格对象在规格列表中的索引
					var objIndex = $scope.entity.tbGoodsDesc.specificationItems.indexOf(object);
                    $scope.entity.tbGoodsDesc.specificationItems.splice(objIndex,1);
				}
			}
		}else{
			//如果为null，说明规格名称不存在于勾选的规格列表中，就创建一个规格对象，添加到列表中
			$scope.entity.tbGoodsDesc.specificationItems.push
			({"attributeName":speciName,"attributeValue":[specOptionName]});
		}
    };

    //生成sku列表

	$scope.createItemList = function () {

		//初始化sku列表
		$scope.entity.tbItemList = [{spec:{},price:2999,num:100,status:'1',isDefault:'0'}];
		//创建勾选的规格列表(规格结果集)
		var itms = $scope.entity.tbGoodsDesc.specificationItems;
		//判断如果勾选的规格列表为空时，将sku列表制空
		if(itms.length == 0){
            $scope.entity.tbItemList = [];
		}
		//遍历勾选的规格列表
		for(var i = 0;i < itms.length;i++){
            $scope.entity.tbItemList = $scope.addColumn($scope.entity.tbItemList,itms[i].attributeName,itms[i].attributeValue);
		}
    };

    //生成sku对象
	//sku对象格式：spec:{"机身内存":"16G" , "网络":"联通3G"};
	$scope.addColumn = function (itemList,speciName,specOptions) {
		//声明一个新的sku集合
		var newList = [];
		//遍历传递过来的sku列表
		for(var i = 0;i<itemList.length;i++){
			//获取sku对象(一条记录就是一个sku对象)
			var oldRow = itemList[i];
			//在遍历勾选的规格对象的规格选项列表
			for(var j = 0;j<specOptions.length;j++){
				//深克隆，生成新的sku对象
				var newRow = JSON.parse(JSON.stringify(oldRow));
				//向新的sku对象中插入规格名称和规格选项
				newRow.spec[speciName]=specOptions[j];
				//向新的sku集合中插入sku对象
				newList.push(newRow);
			}
		}
		return newList;
    };

	//声明显示商品状态数组
	$scope.starts = ['未审核','已审核','审核未通过','关闭'];

	//初始化商品分类数组
	$scope.itemCatArray = [];
	//调用分类服务层方法查询所有分类信息
	$scope.itemCatList = function () {
		itemCatService.findAll().success(function (response) {
			//response=[{id,name,paraId,typeId}]
			//遍历返回的分类集合
			for(var i =0;i< response.length;i++){
				//以分类id为分类数组的索引，给分类数组的索引赋分类名称
                $scope.itemCatArray[response[i].id] = response[i].name;
			}
        })
    };

	//商品上下架
	$scope.updateMarketable =function (isMarketable) {
		goodsService.updateMarketable($scope.ids,isMarketable).success(function (response) {
			if(response.success){
				//上下架成功
				$scope.reloadList();
				//清空商品id数组
				$scope.ids = [];
			}else{
				alert(response.message);
			}
        })
    };

    //创建上下架数组
    $scope.MarketableArray = ['下架','上架'];

});	
