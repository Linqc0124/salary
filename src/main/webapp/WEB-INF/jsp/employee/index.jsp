<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>员工管理</title>
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/4.6.0/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/datatables/1.10.21/css/dataTables.bootstrap4.min.css">
    <style>
        .card {
            margin-bottom: 20px;
        }
        .btn-toolbar {
            margin-bottom: 15px;
        }
        .form-group label.required:after {
            content: " *";
            color: red;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="card">
            <div class="card-header">
                <h5>员工管理</h5>
            </div>
            <div class="card-body">
                <div class="row mb-3">
                    <div class="col-md-8">
                        <div class="btn-toolbar">
                            <div class="btn-group mr-2">
                                <button type="button" class="btn btn-primary" onclick="openAddModal()">
                                    <i class="fas fa-plus"></i> 新增员工
                                </button>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-group">
                            <select id="departmentFilter" class="form-control">
                                <option value="">全部部门</option>
                                <!-- 部门选项将通过Ajax加载 -->
                            </select>
                        </div>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="employeeTable" class="table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>员工编号</th>
                                <th>姓名</th>
                                <th>身份证号</th>
                                <th>手机号</th>
                                <th>部门</th>
                                <th>岗位</th>
                                <th>职务</th>
                                <th>入职日期</th>
                                <th>状态</th>
                                <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                            <!-- 数据将通过Ajax加载 -->
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- 新增员工模态框 -->
    <div class="modal fade" id="addEmployeeModal" tabindex="-1" role="dialog" aria-labelledby="addEmployeeModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addEmployeeModalLabel">新增员工</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="addEmployeeForm">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="employeeNo" class="required">员工编号</label>
                                    <input type="text" class="form-control" id="employeeNo" name="employeeNo" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="name" class="required">姓名</label>
                                    <input type="text" class="form-control" id="name" name="name" required>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="idCard" class="required">身份证号</label>
                                    <input type="text" class="form-control" id="idCard" name="idCard" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="phone" class="required">手机号</label>
                                    <input type="text" class="form-control" id="phone" name="phone" required>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="departmentId" class="required">部门</label>
                                    <select class="form-control" id="departmentId" name="departmentId" required>
                                        <option value="">请选择</option>
                                        <!-- 部门选项将通过Ajax加载 -->
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="position" class="required">岗位</label>
                                    <input type="text" class="form-control" id="position" name="position" required>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="jobTitle" class="required">职务</label>
                                    <input type="text" class="form-control" id="jobTitle" name="jobTitle" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="entryDateStr" class="required">入职日期</label>
                                    <input type="date" class="form-control" id="entryDateStr" name="entryDateStr" required>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="address">住址</label>
                            <textarea class="form-control" id="address" name="address" rows="3"></textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" onclick="saveEmployee()">保存</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 编辑员工模态框 -->
    <div class="modal fade" id="editEmployeeModal" tabindex="-1" role="dialog" aria-labelledby="editEmployeeModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editEmployeeModalLabel">编辑员工</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="editEmployeeForm">
                        <input type="hidden" id="editId" name="id">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editEmployeeNo" class="required">员工编号</label>
                                    <input type="text" class="form-control" id="editEmployeeNo" name="employeeNo" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editName" class="required">姓名</label>
                                    <input type="text" class="form-control" id="editName" name="name" required>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editIdCard" class="required">身份证号</label>
                                    <input type="text" class="form-control" id="editIdCard" name="idCard" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editPhone" class="required">手机号</label>
                                    <input type="text" class="form-control" id="editPhone" name="phone" required>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editDepartmentId" class="required">部门</label>
                                    <select class="form-control" id="editDepartmentId" name="departmentId" required>
                                        <option value="">请选择</option>
                                        <!-- 部门选项将通过Ajax加载 -->
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editPosition" class="required">岗位</label>
                                    <input type="text" class="form-control" id="editPosition" name="position" required>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editJobTitle" class="required">职务</label>
                                    <input type="text" class="form-control" id="editJobTitle" name="jobTitle" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editEntryDateStr" class="required">入职日期</label>
                                    <input type="date" class="form-control" id="editEntryDateStr" name="entryDateStr" required>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editStatus">状态</label>
                                    <select class="form-control" id="editStatus" name="status">
                                        <option value="1">在职</option>
                                        <option value="0">离职</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="editAddress">住址</label>
                            <textarea class="form-control" id="editAddress" name="address" rows="3"></textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" onclick="updateEmployee()">保存</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 查看员工详情模态框 -->
    <div class="modal fade" id="viewEmployeeModal" tabindex="-1" role="dialog" aria-labelledby="viewEmployeeModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="viewEmployeeModalLabel">员工详情</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>员工编号：</strong><span id="viewEmployeeNo"></span></p>
                            <p><strong>姓名：</strong><span id="viewName"></span></p>
                            <p><strong>身份证号：</strong><span id="viewIdCard"></span></p>
                            <p><strong>手机号：</strong><span id="viewPhone"></span></p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>部门：</strong><span id="viewDepartment"></span></p>
                            <p><strong>岗位：</strong><span id="viewPosition"></span></p>
                            <p><strong>职务：</strong><span id="viewJobTitle"></span></p>
                            <p><strong>入职日期：</strong><span id="viewEntryDate"></span></p>
                        </div>
                    </div>
                    <p><strong>住址：</strong><span id="viewAddress"></span></p>
                    <p><strong>状态：</strong><span id="viewStatus"></span></p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>

    <script src="/static/jquery.min.js"></script>
    <script src="/static/bootstrap.bundle.min.js"></script>
    <script src="/static/jquery.dataTables.min.js"></script>
    <script src="/static/dataTables.bootstrap4.min.js"></script>
    <script>
        let table;
        let departments = [];

        $(function() {


            // 初始化DataTable
            table = $('#employeeTable').DataTable({
                ajax: {
                    url: '${pageContext.request.contextPath}/employee/list',
                    dataSrc: function(json) {
                        return json.data.records;
                    }
                },
                columns: [
                    { data: 'id' },
                    { data: 'employeeNo' },
                    { data: 'name' },
                    { data: 'idCard' },
                    { data: 'phone' },
                    {
                        data: 'departmentId',
                        render: function(data) {
                            const department = departments.find(d => d.id === data);
                            return department ? department.name : '';
                        }
                    },
                    { data: 'position' },
                    { data: 'jobTitle' },
                    {
                        data: 'entryDate',
                        render: function(data) {
                            return new Date(data).toLocaleDateString();
                        }
                    },
                    {
                        data: 'status',
                        render: function(data) {
                            return data === 1 ?
                                '<span class="badge badge-success">在职</span>' :
                                '<span class="badge badge-danger">离职</span>';
                        }
                    },
                    {
                        data: null,
                        render: function(data, type, row) {
                            return '<div class="btn-group btn-group-sm">' +
                                '<button type="button" class="btn btn-info" onclick="viewEmployee(' + row.id + ')"><i class="fas fa-eye"></i> 查看</button>' +
                                '<button type="button" class="btn btn-primary" onclick="editEmployee(' + row.id + ')"><i class="fas fa-edit"></i> 编辑</button>' +
                                '<button type="button" class="btn btn-danger" onclick="deleteEmployee(' + row.id + ')"><i class="fas fa-trash"></i> 删除</button>' +
                                '</div>';
                        }
                    }
                ],
                language: {

                }
            });
            // 加载部门数据
            loadDepartments();
            // 部门筛选
            $('#departmentFilter').change(function() {
                const departmentId = $(this).val();
                table.ajax.url('${pageContext.request.contextPath}/employee/list?departmentId=' + departmentId).load();
            });
        });

        // 加载部门数据
        function loadDepartments() {
            $.ajax({
                url: '${pageContext.request.contextPath}/department/all',
                type: 'GET',
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        departments = res.data;

                        // 填充部门下拉框
                        let options = '<option value="">请选择</option>';
                        let filterOptions = '<option value="">全部部门</option>';

                        departments.forEach(function(dept) {
                            options += '<option value="' + dept.id + '">' + dept.name + '</option>';
                            filterOptions += '<option value="' + dept.id + '">' + dept.name + '</option>';
                        });

                        $('#departmentId').html(options);
                        $('#editDepartmentId').html(options);
                        $('#departmentFilter').html(filterOptions);
                    }
                }
            });
        }

        // 打开新增员工模态框
        function openAddModal() {
            $('#addEmployeeForm')[0].reset();
            $('#addEmployeeModal').modal('show');
        }

        // 保存员工
        function saveEmployee() {
            const formData = $('#addEmployeeForm').serialize();

            $.ajax({
                url: '${pageContext.request.contextPath}/employee/add',
                type: 'POST',
                data: formData,
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        $('#addEmployeeModal').modal('hide');
                        table.ajax.reload();
                        alert('添加成功');
                    } else {
                        alert(res.message);
                    }
                },
                error: function() {
                    alert('系统错误，请稍后再试');
                }
            });
        }

        // 查看员工详情
        function viewEmployee(id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/employee/detail/' + id,
                type: 'GET',
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        const employee = res.data;
                        console.log('查询员工',employee)
                        $('#viewEmployeeNo').text(employee.employeeNo);
                        $('#viewName').text(employee.name);
                        $('#viewIdCard').text(employee.idCard);
                        $('#viewPhone').text(employee.phone);
                        // $('#viewDepartment').text(department ? department.name : '');
                        $('#viewPosition').text(employee.position);
                        $('#viewJobTitle').text(employee.jobTitle);
                       $('#viewEntryDate').text(new Date(employee.entryDate).toLocaleDateString());
                        $('#viewAddress').text(employee.address);
                        $('#viewStatus').text(employee.status === 1 ? '在职' : '离职');

                        $('#viewEmployeeModal').modal('show');
                    } else {
                        alert(res.message);
                    }
                },
                error: function() {
                    alert('系统错误，请稍后再试');
                }
            });
        }

        // 编辑员工
        function editEmployee(id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/employee/edit/' + id,
                type: 'GET',
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        const employee = res.data;
                        const entryDate = new Date(employee.entryDate);
                        const formattedDate = entryDate.toISOString().split('T')[0];

                        $('#editId').val(employee.id);
                        $('#editEmployeeNo').val(employee.employeeNo);
                        $('#editName').val(employee.name);
                        $('#editIdCard').val(employee.idCard);
                        $('#editPhone').val(employee.phone);
                        $('#editDepartmentId').val(employee.departmentId);
                        $('#editPosition').val(employee.position);
                        $('#editJobTitle').val(employee.jobTitle);
                        $('#editEntryDateStr').val(formattedDate);
                        $('#editAddress').val(employee.address);
                        $('#editStatus').val(employee.status);

                        $('#editEmployeeModal').modal('show');
                    } else {
                        alert(res.message);
                    }
                },
                error: function() {
                    alert('系统错误，请稍后再试');
                }
            });
        }

        // 更新员工
        function updateEmployee() {
            const formData = $('#editEmployeeForm').serialize();

            $.ajax({
                url: '${pageContext.request.contextPath}/employee/edit',
                type: 'POST',
                data: formData,
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        $('#editEmployeeModal').modal('hide');
                        table.ajax.reload();
                        alert('更新成功');
                    } else {
                        alert(res.message);
                    }
                },
                error: function() {
                    alert('系统错误，请稍后再试');
                }
            });
        }

        // 删除员工
        function deleteEmployee(id) {
            if (confirm('确定要删除该员工吗？')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/employee/delete/' + id,
                    type: 'POST',
                    dataType: 'json',
                    success: function(res) {
                        if (res.code === 200) {
                            table.ajax.reload();
                            alert('删除成功');
                        } else {
                            alert(res.message);
                        }
                    },
                    error: function() {
                        alert('系统错误，请稍后再试');
                    }
                });
            }
        }
    </script>
</body>
</html>
