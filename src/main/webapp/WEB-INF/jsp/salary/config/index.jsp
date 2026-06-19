<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>工资配置</title>
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
                <h5>工资配置</h5>
            </div>
            <div class="card-body">
                <div class="btn-toolbar">
                    <div class="btn-group mr-2">
                        <button type="button" class="btn btn-primary" onclick="openAddModal()">
                            <i class="fas fa-plus"></i> 新增工资配置
                        </button>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="salaryConfigTable" class="table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>员工</th>
                                <th>基本工资</th>
                                <th>岗位工资</th>
                                <th>午餐补贴</th>
                                <th>全勤奖</th>
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

    <!-- 新增工资配置模态框 -->
    <div class="modal fade" id="addSalaryConfigModal" tabindex="-1" role="dialog" aria-labelledby="addSalaryConfigModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addSalaryConfigModalLabel">新增工资配置</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="addSalaryConfigForm">
                        <div class="form-group">
                            <label for="employeeId">员工</label>
                            <select class="form-control" id="employeeId" name="employeeId" required>
                                <option value="">请选择</option>
                                <!-- 员工选项将通过Ajax加载 -->
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="baseSalary">基本工资</label>
                            <input type="number" class="form-control" id="baseSalary" name="baseSalary" required min="0" step="0.01">
                        </div>
                        <div class="form-group">
                            <label for="positionSalary">岗位工资</label>
                            <input type="number" class="form-control" id="positionSalary" name="positionSalary" required min="0" step="0.01">
                        </div>
                        <div class="form-group">
                            <label for="lunchSubsidy">午餐补贴</label>
                            <input type="number" class="form-control" id="lunchSubsidy" name="lunchSubsidy" min="0" step="0.01">
                        </div>
                        <div class="form-group">
                            <label for="fullAttendanceBonus">全勤奖</label>
                            <input type="number" class="form-control" id="fullAttendanceBonus" name="fullAttendanceBonus" min="0" step="0.01">
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" onclick="saveSalaryConfig()">保存</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 编辑工资配置模态框 -->
    <div class="modal fade" id="editSalaryConfigModal" tabindex="-1" role="dialog" aria-labelledby="editSalaryConfigModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editSalaryConfigModalLabel">编辑工资配置</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="editSalaryConfigForm">
                        <input type="hidden" id="editId" name="id">
                        <div class="form-group">
                            <label for="editEmployeeName">员工</label>
                            <input type="text" class="form-control" id="editEmployeeName" readonly>
                        </div>
                        <div class="form-group">
                            <label for="editBaseSalary">基本工资</label>
                            <input type="number" class="form-control" id="editBaseSalary" name="baseSalary" required min="0" step="0.01">
                        </div>
                        <div class="form-group">
                            <label for="editPositionSalary">岗位工资</label>
                            <input type="number" class="form-control" id="editPositionSalary" name="positionSalary" required min="0" step="0.01">
                        </div>
                        <div class="form-group">
                            <label for="editLunchSubsidy">午餐补贴</label>
                            <input type="number" class="form-control" id="editLunchSubsidy" name="lunchSubsidy" min="0" step="0.01">
                        </div>
                        <div class="form-group">
                            <label for="editFullAttendanceBonus">全勤奖</label>
                            <input type="number" class="form-control" id="editFullAttendanceBonus" name="fullAttendanceBonus" min="0" step="0.01">
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
                    <button type="button" class="btn btn-primary" onclick="updateSalaryConfig()">保存</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 查看工资配置详情模态框 -->
    <div class="modal fade" id="viewSalaryConfigModal" tabindex="-1" role="dialog" aria-labelledby="viewSalaryConfigModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="viewSalaryConfigModalLabel">工资配置详情</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>员工：</strong><span id="viewEmployeeName"></span></p>
                            <p><strong>基本工资：</strong><span id="viewBaseSalary"></span></p>
                            <p><strong>岗位工资：</strong><span id="viewPositionSalary"></span></p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>午餐补贴：</strong><span id="viewLunchSubsidy"></span></p>
                            <p><strong>全勤奖：</strong><span id="viewFullAttendanceBonus"></span></p>
                            <p><strong>状态：</strong><span id="viewStatus"></span></p>
                        </div>
                    </div>
                    <p><strong>创建时间：</strong><span id="viewCreateTime"></span></p>
                    <p><strong>更新时间：</strong><span id="viewUpdateTime"></span></p>
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
        let employees = [];

        $(function() {
            // 加载员工数据
            loadEmployees();

            // 初始化DataTable
            setTimeout(()=>{
                table = $('#salaryConfigTable').DataTable({
                    ajax: {
                        url: '${pageContext.request.contextPath}/salary/config/list',
                        dataSrc: function(json) {
                            return json.data.records;
                        }
                    },
                    columns: [
                        { data: 'id' },
                        {
                            data: 'employeeId',
                            render: function(data) {
                                const employee = employees.find(e => e.id === data);
                                return employee ? employee.name : '';
                            }
                        },
                        {
                            data: 'baseSalary',
                            render: function(data) {
                                return data.toFixed(2);
                            }
                        },
                        {
                            data: 'positionSalary',
                            render: function(data) {
                                return data.toFixed(2);
                            }
                        },
                        {
                            data: 'lunchSubsidy',
                            render: function(data) {
                                return data ? data.toFixed(2) : '0.00';
                            }
                        },
                        {
                            data: 'fullAttendanceBonus',
                            render: function(data) {
                                return data ? data.toFixed(2) : '0.00';
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
                            data: 'createTime',
                            render: function(data) {
                                return new Date(data).toLocaleString();
                            }
                        },
                        {
                            data: null,
                            render: function(data, type, row) {
                                return '<div class="btn-group btn-group-sm">' +
                                    '<button type="button" class="btn btn-info" onclick="viewSalaryConfig(' + row.id + ')"><i class="fas fa-eye"></i> 查看</button>' +
                                    '<button type="button" class="btn btn-primary" onclick="editSalaryConfig(' + row.id + ')"><i class="fas fa-edit"></i> 编辑</button>' +
                                    '<button type="button" class="btn btn-warning" onclick="toggleSalaryConfig(' + row.id + ')"><i class="fas fa-power-off"></i> ' + (row.status === 1 ? '禁用' : '启用') + '</button>' +
                                    '</div>';
                            }
                        }
                    ],
                    language: {

                    }
                });
            },100)
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

        // 打开新增工资配置模态框
        function openAddModal() {
            $('#addSalaryConfigForm')[0].reset();
            $('#addSalaryConfigModal').modal('show');
        }

        // 保存工资配置
        function saveSalaryConfig() {
            const formData = $('#addSalaryConfigForm').serialize();

            $.ajax({
                url: '${pageContext.request.contextPath}/salary/config/add',
                type: 'POST',
                data: formData,
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        $('#addSalaryConfigModal').modal('hide');
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

        // 查看工资配置详情
        function viewSalaryConfig(id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/salary/config/detail/' + id,
                type: 'GET',
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        const salaryConfig = res.data;

                        $('#viewBaseSalary').text(salaryConfig.baseSalary.toFixed(2));
                        $('#viewPositionSalary').text(salaryConfig.positionSalary.toFixed(2));
                        $('#viewLunchSubsidy').text(salaryConfig.lunchSubsidy ? salaryConfig.lunchSubsidy.toFixed(2) : '0.00');
                        $('#viewFullAttendanceBonus').text(salaryConfig.fullAttendanceBonus ? salaryConfig.fullAttendanceBonus.toFixed(2) : '0.00');
                        $('#viewStatus').text(salaryConfig.status === 1 ? '启用' : '禁用');
                        $('#viewCreateTime').text(new Date(salaryConfig.createTime).toLocaleString());
                        $('#viewUpdateTime').text(new Date(salaryConfig.updateTime).toLocaleString());

                        $('#viewSalaryConfigModal').modal('show');
                    } else {
                        alert(res.message);
                    }
                },
                error: function() {
                    alert('系统错误，请稍后再试');
                }
            });
        }

        // 编辑工资配置
        function editSalaryConfig(id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/salary/config/edit/' + id,
                type: 'GET',
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        const salaryConfig = res.data;

                        $('#editId').val(salaryConfig.id);
                        $('#editEmployeeName').val(salaryConfig.employeeId);
                        $('#editBaseSalary').val(salaryConfig.baseSalary);
                        $('#editPositionSalary').val(salaryConfig.positionSalary);
                        $('#editLunchSubsidy').val(salaryConfig.lunchSubsidy);
                        $('#editFullAttendanceBonus').val(salaryConfig.fullAttendanceBonus);
                        $('#editStatus').val(salaryConfig.status);

                        $('#editSalaryConfigModal').modal('show');
                    } else {
                        alert(res.message);
                    }
                },
                error: function() {
                    alert('系统错误，请稍后再试');
                }
            });
        }

        // 更新工资配置
        function updateSalaryConfig() {
            const formData = $('#editSalaryConfigForm').serialize();

            $.ajax({
                url: '${pageContext.request.contextPath}/salary/config/edit',
                type: 'POST',
                data: formData,
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        $('#editSalaryConfigModal').modal('hide');
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

        // 启用/禁用工资配置
        function toggleSalaryConfig(id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/salary/config/toggle/' + id,
                type: 'POST',
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        table.ajax.reload();
                        alert('操作成功');
                    } else {
                        alert(res.message);
                    }
                },
                error: function() {
                    alert('系统错误，请稍后再试');
                }
            });
        }
    </script>
</body>
</html>
