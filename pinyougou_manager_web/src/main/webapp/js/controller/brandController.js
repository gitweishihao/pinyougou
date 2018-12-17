
//自定义控制器
app.controller("brandController",function ($scope,$controller,brandService) {
    //控制器继承代码  参数一：继承的父控制器  参数二：固定写法，共享$scope对象
    $controller("baseController",{$scope:$scope});

    //定义查询所有品牌列表的方法
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            //将结果集赋给list集合
            $scope.list=response;
        })
    };

    //定义方法分页查询品牌
    $scope.findPage = function (pageNum,pageSize) {
        brandService.findPage(pageNum,pageSize).success(function (response) {
            //将响应结果总记录数赋值给angularjs分页配置
            $scope.paginationConf.totalItems = response.total;
            //将响应结果的当前页数据列表赋给list集合
            $scope.list = response.rows;
        })
    };

    //定义保存方法
    $scope.save = function () {
        var method = null;
        //根据方法是否有id判断是添加方法还是修改保存
        if($scope.entity.id!=null){
            //是修改保存方法
            method = brandService.update($scope.entity);
        }else{
            //是添加保存方法
            method = brandService.add($scope.entity);
        }
        //发送请求
        method.success(function (response) {
            //判断
            if(response.success){
                //添加成功，重新加载品牌列表
                $scope.reloadList();
            }else{
                //展示提示信息
                alert(response.massage);
            }
        })
    };

    //根据id查询品牌
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        })
    };

    //删除品牌
    $scope.dele = function () {
        //判断是否要删除
        if(confirm("您确定要删除吗？")){
           brandService.dele($scope.ids).success(function (response) {
                if(response.success){
                    //删除成功，重新加载品牌列表
                    $scope.reloadList();
                }else{
                    //删除失败，展现提示信息
                    alert(response.massage)
                }
            })
        }
    }
});