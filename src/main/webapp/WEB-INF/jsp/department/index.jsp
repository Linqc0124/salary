<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>部门管理</title>
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
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="card">
            <div class="card-header">
                <h5>部门管理</h5>
            </div>
            <div class="card-body">
                <div class="btn-toolbar">
                    <div class="btn-group mr-2">
                        <button type="button" class="btn btn-primary" onclick="openAddModal()">
                            <i class="fas fa-plus"></i> 新增部门
                        </button>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="departmentTable" class="table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>部门名称</th>
                                <th>部门代码</th>
                                <th>描述</th>
                                <th>状态</th>
                                <th>创建时间</th>
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

    <!-- 新增部门模态框 -->
    <div class="modal fade" id="addDepartmentModal" tabindex="-1" role="dialog" aria-labelledby="addDepartmentModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addDepartmentModalLabel">新增部门</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="addDepartmentForm">
                        <div class="form-group">
                            <label for="name">部门名称</label>
                            <input type="text" class="form-control" id="name" name="name" required>
                        </div>
                        <div class="form-group">
                            <label for="code">部门代码</label>
                            <input type="text" class="form-control" id="code" name="code" required>
                        </div>
                        <div class="form-group">
                            <label for="description">描述</label>
                            <textarea class="form-control" id="description" name="description" rows="3"></textarea>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" onclick="saveDepartment()">保存</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 编辑部门模态框 -->
    <div class="modal fade" id="editDepartmentModal" tabindex="-1" role="dialog" aria-labelledby="editDepartmentModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editDepartmentModalLabel">编辑部门</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="editDepartmentForm">
                        <input type="hidden" id="editId" name="id">
                        <div class="form-group">
                            <label for="editName">部门名称</label>
                            <input type="text" class="form-control" id="editName" name="name" required>
                        </div>
                        <div class="form-group">
                            <label for="editCode">部门代码</label>
                            <input type="text" class="form-control" id="editCode" name="code" required>
                        </div>
                        <div class="form-group">
                            <label for="editDescription">描述</label>
                            <textarea class="form-control" id="editDescription" name="description" rows="3"></textarea>
                        </div>
                        <div class="form-group">
                            <label for="editStatus">状态</label>
                            <select class="form-control" id="editStatus" name="status">
                                <option value="1">启用</option>
                                <option value="0">禁用</option>
                            </select>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" onclick="updateDepartment()">保存</button>
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

        $(function() {
            loadEmployees();
            // 加载员工数据
            setTimeout(()=>{
                // 初始化DataTable
                table = $('#departmentTable').DataTable({
                    ajax: {
                        url: '${pageContext.request.contextPath}/department/list',
                        dataSrc: function(json) {
                            return json.data.records;
                        }
                    },
                    columns: [
                        { data: 'id' },
                        { data: 'name' },
                        { data: 'code' },
                        { data: 'description' },
                        {
                            data: 'status',
                            render: function(data) {
                                return data === 1 ?
                                    '<span class="badge badge-success">启用</span>' :
                                    '<span class="badge badge-danger">禁用</span>';
                            }
                        },
                        {
                            data: 'createTime',
                            render: function(data) {
                                return new Date(data).toLocaleString();
                            }
                        },
                        {
                            data: null,
                            render: function(data, type, row) {
                                return '<div class="btn-group btn-group-sm">' +
                                    '<button type="button" class="btn btn-info" onclick="editDepartment(' + row.id + ')"><i class="fas fa-edit"></i> 编辑</button>' +
                                    '<button type="button" class="btn btn-danger" onclick="deleteDepartment(' + row.id + ')"><i class="fas fa-trash"></i> 删除</button>' +
                                    '</div>';
                            }
                        }
                    ],
                });
            },600)


        });


        // 加载员工数据
        function loadEmployees() {
            $.ajax({
                url: '${pageContext.request.contextPath}/employee/all',
                type: 'GET',
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        employees = res.data;

                        // 填充员工下拉框
                        let options = '<option value="">请选择</option>';

                        employees.forEach(function(emp) {
                            if (emp.status === 1) { // 只显示在职员工
                                options += '<option value="' + emp.id + '">' + emp.name + ' (' + emp.employeeNo + ')</option>';
                            }
                        });

                        $('#employeeId').html(options);
                    }
                }
            });
        }
        // 打开新增部门模态框
        function openAddModal() {
            $('#addDepartmentForm')[0].reset();
            $('#addDepartmentModal').modal('show');
        }

        // 保存部门
        function saveDepartment() {
            const formData = $('#addDepartmentForm').serialize();

            $.ajax({
                url: '${pageContext.request.contextPath}/department/add',
                type: 'POST',
                data: formData,
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        $('#addDepartmentModal').modal('hide');
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

        // 编辑部门
        function editDepartment(id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/department/edit/' + id,
                type: 'GET',
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        const department = res.data;
                        $('#editId').val(department.id);
                        $('#editName').val(department.name);
                        $('#editCode').val(department.code);
                        $('#editDescription').val(department.description);
                        $('#editStatus').val(department.status);

                        $('#editDepartmentModal').modal('show');
                    } else {
                        alert(res.message);
                    }
                },
                error: function() {
                    alert('系统错误，请稍后再试');
                }
            });
        }

        // 更新部门
        function updateDepartment() {
            const formData = $('#editDepartmentForm').serialize();

            $.ajax({
                url: '${pageContext.request.contextPath}/department/edit',
                type: 'POST',
                data: formData,
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        $('#editDepartmentModal').modal('hide');
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

        // 删除部门
        function deleteDepartment(id) {
            if (confirm('确定要删除该部门吗？')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/department/delete/' + id,
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
