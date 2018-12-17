//自定义规格服务
app.service("specificationService",function ($http) {

    //分页查询所有
    this.findPage = function (pageNum,pageSize) {
       return $http.get("../specification/findPage.do?pageNum="+pageNum+"&pageSize="+pageSize);
    };

    //条件查询
    this.search = function (searchEntity,pageNum,pageSize) {
        return $http.post("../specification/search.do?pageNum="+pageNum+"&pageSize="+pageSize,searchEntity);
    };
    //添加保存
    this.add = function (entity) {
        return $http.post("../specification/add.do",entity);
    };
    //修改保存
    this.update = function (entity) {
        return $http.post("../specification/update.do",entity);
    };
    //根据id查询
    this.findOne = function (id) {
        return $http.get("../specification/findOne.do?id="+id);
    };
    //根据规格id数组删除
    this.dele = function (ids) {
        return $http.get("../specification/delete.do?ids="+ids);
    };
    //查询模板关联规格数据列表
    this.selectSpecificationList = function () {
        return $http.get("../specification/selectSpecificationList.do");
    }
});