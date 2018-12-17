//自定义服务	参数一：服务名称 参数二：服务要处理的事情 （发起http请求）
app.service("brandService",function ($http) {
    //查询所有品牌
    this.findAll = function () {
        return $http.get("../tbBrand/findAll.do");
    };
    //分页查询品牌
    this.findPage = function (pageNum,pageSize) {
        return $http.get("../tbBrand/findPage.do?pageNum="+pageNum+"&pageSize="+pageSize);
    };
    //添加保存品牌
    this.add = function (entiry) {
        return $http.post("../tbBrand/add.do",entiry);
    };
    //修改保存
    this.update = function (entity) {
        return $http.post("../tbBrand/update.do",entity);
    };
    //根据id查询品牌
    this.findOne = function (id) {
        return $http.get("../tbBrand/findOne.do?id="+id)
    };
    //删除品牌
    this.dele = function (ids) {
        return $http.get("../tbBrand/delete.do?ids="+ids)
    };
    //查询模板关联品牌数据列表
    this.selectBrandList =function () {
        return $http.get("../tbBrand/selectBrandList.do");
    }

});
