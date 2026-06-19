<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>用户管理</title>
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
                <h5>用户管理</h5>
            </div>
            <div class="card-body">
                <div class="btn-toolbar">
                    <div class="btn-group mr-2">
                        <button type="button" class="btn btn-primary" onclick="openAddModal()">
                            <i class="fas fa-plus"></i> 新增用户
                        </button>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="userTable" class="table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>用户名</th>
                                <th>真实姓名</th>
                                <th>角色</th>
                                <th>状态</th>
                                <th>最后密码重置时间</th>
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

    <!-- 新增用户模态框 -->
    <div class="modal fade" id="addUserModal" tabindex="-1" role="dialog" aria-labelledby="addUserModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addUserModalLabel">新增用户</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="addUserForm">
                        <div class="form-group">
                            <label for="username">用户名</label>
                            <input type="text" class="form-control" id="username" name="username" required>
                        </div>
                        <div class="form-group">
                            <label for="password">密码</label>
                            <input type="password" class="form-control" id="password" name="password" required>
                        </div>
                        <div class="form-group">
                            <label for="realName">真实姓名</label>
                            <input type="text" class="form-control" id="realName" name="realName" required>
                        </div>
                        <div class="form-group">
                            <label for="role">角色</label>
                            <select class="form-control" id="role" name="role" required>
                                <option value="">请选择</option>
                                <option value="ADMIN">系统管理员</option>
                                <option value="HR">人事管理员</option>
                                <option value="FINANCE">财务管理员</option>
                                <option value="MANAGER">总经理</option>
                                <option value="AUDIT">审计员</option>
                            </select>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" onclick="saveUser()">保存</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 编辑用户模态框 -->
    <div class="modal fade" id="editUserModal" tabindex="-1" role="dialog" aria-labelledby="editUserModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editUserModalLabel">编辑用户</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="editUserForm">
                        <input type="hidden" id="editId" name="id">
                        <div class="form-group">
                            <label for="editUsername">用户名</label>
                            <input type="text" class="form-control" id="editUsername" name="username" readonly>
                        </div>
                        <div class="form-group">
                            <label for="editRealName">真实姓名</label>
                            <input type="text" class="form-control" id="editRealName" name="realName" required>
                        </div>
                        <div class="form-group">
                            <label for="editRole">角色</label>
                            <select class="form-control" id="editRole" name="role" required>
                                <option value="">请选择</option>
                                <option value="ADMIN">系统管理员</option>
                                <option value="HR">人事管理员</option>
                                <option value="FINANCE">财务管理员</option>
                                <option value="MANAGER">总经理</option>
                                <option value="AUDIT">审计员</option>
                            </select>
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
                    <button type="button" class="btn btn-primary" onclick="updateUser()">保存</button>
<%--                    <button type="button" class="btn btn-warning" onclick="resetPassword()">重置密码</button>--%>
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
            // 初始化DataTable
            table = $('#userTable').DataTable({
                ajax: {
                    url: '${pageContext.request.contextPath}/user/list',
                    dataSrc: function(json) {
                        return json.data.records;
                    }
                },
                columns: [
                    { data: 'id' },
                    { data: 'username' },
                    { data: 'realName' },
                    {
                        data: 'role',
                        render: function(data) {
                            switch(data) {
                                case 'ADMIN': return '系统管理员';
                                case 'HR': return '人事管理员';
                                case 'FINANCE': return '财务管理员';
                                case 'MANAGER': return '总经理';
                                case 'AUDIT': return '审计员';
                                default: return data;
                            }
                        }
                    },
                    {
                        data: 'status',
                        render: function(data) {
                            return data === 1 ?
                                '<span class="badge badge-success">启用</span>' :
                                '<span class="badge badge-danger">禁用</span>';
                        }
                    },
                    {
                        data: 'lastPasswordResetDate',
                        render: function(data) {
                            return new Date(data).toLocaleString();
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
                                '<button type="button" class="btn btn-primary" onclick="editUser(' + row.id + ')"><i class="fas fa-edit"></i> 编辑</button>' +
                                '<button type="button" class="btn btn-danger" onclick="deleteUser(' + row.id + ')"><i class="fas fa-trash"></i> 删除</button>' +
                                '</div>';
                        }
                    }
                ],
                language: {

                }
            });
        });

        // 打开新增用户模态框
        function openAddModal() {
            $('#addUserForm')[0].reset();
            $('#addUserModal').modal('show');
        }

        // 保存用户
        function saveUser() {
            const formData = $('#addUserForm').serialize();

            $.ajax({
                url: '${pageContext.request.contextPath}/user/add',
                type: 'POST',
                data: formData,
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        $('#addUserModal').modal('hide');
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

        // 编辑用户
        function editUser(id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/user/edit/' + id,
                type: 'GET',
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        const user = res.data;
                        $('#editId').val(user.id);
                        $('#editUsername').val(user.username);
                        $('#editRealName').val(user.realName);
                        $('#editRole').val(user.role);
                        $('#editStatus').val(user.status);

                        $('#editUserModal').modal('show');
                    } else {
                        alert(res.message);
                    }
                },
                error: function() {
                    alert('系统错误，请稍后再试');
                }
            });
        }

        // 更新用户
        function updateUser() {
            const formData = $('#editUserForm').serialize();

            $.ajax({
                url: '${pageContext.request.contextPath}/user/edit',
                type: 'POST',
                data: formData,
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        $('#editUserModal').modal('hide');
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

        // 重置密码
        function resetPassword() {
            const id = $('#editId').val();

            if (confirm('确定要重置该用户的密码吗？')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/user/reset-password/' + id,
                    type: 'POST',
                    dataType: 'json',
                    success: function(res) {
                        if (res.code === 200) {
                            alert('密码重置成功，新密码已发送至用户');
                            table.ajax.reload();
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

        // 删除用户
        function deleteUser(id) {
            if (confirm('确定要删除该用户吗？')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/user/delete/' + id,
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
