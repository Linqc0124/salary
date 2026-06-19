<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>专项附加扣除</title>
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
                <h5>专项附加扣除</h5>
            </div>
            <div class="card-body">
                <div class="row mb-3">
                    <div class="col-md-4">
                        <div class="btn-toolbar">
                            <div class="btn-group mr-2">
                                <button type="button" class="btn btn-primary" onclick="openAddModal()">
                                    <i class="fas fa-plus"></i> 新增专项附加扣除
                                </button>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-group">
                            <select id="employeeFilter" class="form-control">
                                <option value="">全部员工</option>
                                <!-- 员工选项将通过Ajax加载 -->
                            </select>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="form-group">
                            <select id="yearFilter" class="form-control">
                                <option value="">全部年份</option>
                                <option value="2023">2023年</option>
                                <option value="2024">2024年</option>
                                <option value="2025">2025年</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="deductionTable" class="table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>员工</th>
                                <th>年份</th>
                                <th>子女教育</th>
                                <th>继续教育</th>
                                <th>住房贷款利息</th>
                                <th>住房租金</th>
                                <th>赡养老人</th>
                                <th>医疗费用</th>
                                <th>子女3岁以下</th>
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

    <!-- 新增专项附加扣除模态框 -->
    <div class="modal fade" id="addDeductionModal" tabindex="-1" role="dialog" aria-labelledby="addDeductionModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="addDeductionModalLabel">新增专项附加扣除</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="addDeductionForm">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="employeeId">员工</label>
                                    <select class="form-control" id="employeeId" name="employeeId" required>
                                        <option value="">请选择</option>
                                        <!-- 员工选项将通过Ajax加载 -->
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="year">年份</label>
                                    <select class="form-control" id="year" name="year" required>
                                        <option value="">请选择</option>
                                        <option value="2023">2023年</option>
                                        <option value="2024">2024年</option>
                                        <option value="2025">2025年</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="childrenEducation">子女教育（每月）</label>
                                    <input type="number" class="form-control" id="childrenEducation" name="childrenEducation" value="0" min="0" step="0.01">
                                    <small class="form-text text-muted">每个子女每月1000元</small>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="continuingEducation">继续教育（每月）</label>
                                    <input type="number" class="form-control" id="continuingEducation" name="continuingEducation" value="0" min="0" step="0.01">
                                    <small class="form-text text-muted">学历教育每月400元，技能培训每年3600元</small>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="housingLoan">住房贷款利息（每月）</label>
                                    <input type="number" class="form-control" id="housingLoan" name="housingLoan" value="0" min="0" step="0.01">
                                    <small class="form-text text-muted">每月1000元</small>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="housingRent">住房租金（每月）</label>
                                    <input type="number" class="form-control" id="housingRent" name="housingRent" value="0" min="0" step="0.01">
                                    <small class="form-text text-muted">一线城市每月1500元，二线城市每月1100元，三线城市每月800元</small>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="elderlyCare">赡养老人（每月）</label>
                                    <input type="number" class="form-control" id="elderlyCare" name="elderlyCare" value="0" min="0" step="0.01">
                                    <small class="form-text text-muted">独生子女每月2000元，非独生子女分摊每月2000元</small>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="medicalExpense">医疗费用（每月）</label>
                                    <input type="number" class="form-control" id="medicalExpense" name="medicalExpense" value="0" min="0" step="0.01">
                                    <small class="form-text text-muted">年度医疗费用超过15000元的部分，可按每年60000元限额据实扣除</small>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="childCare">子女3岁以下（每月）</label>
                                    <input type="number" class="form-control" id="childCare" name="childCare" value="0" min="0" step="0.01">
                                    <small class="form-text text-muted">每个3岁以下婴幼儿每月1000元</small>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" onclick="saveDeduction()">保存</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 编辑专项附加扣除模态框 -->
    <div class="modal fade" id="editDeductionModal" tabindex="-1" role="dialog" aria-labelledby="editDeductionModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editDeductionModalLabel">编辑专项附加扣除</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <form id="editDeductionForm">
                        <input type="hidden" id="editId" name="id">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editEmployeeName">员工</label>
                                    <input type="text" class="form-control" id="editEmployeeName" readonly>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editYear">年份</label>
                                    <input type="text" class="form-control" id="editYear" readonly>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editChildrenEducation">子女教育（每月）</label>
                                    <input type="number" class="form-control" id="editChildrenEducation" name="childrenEducation" min="0" step="0.01">
                                    <small class="form-text text-muted">每个子女每月1000元</small>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editContinuingEducation">继续教育（每月）</label>
                                    <input type="number" class="form-control" id="editContinuingEducation" name="continuingEducation" min="0" step="0.01">
                                    <small class="form-text text-muted">学历教育每月400元，技能培训每年3600元</small>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editHousingLoan">住房贷款利息（每月）</label>
                                    <input type="number" class="form-control" id="editHousingLoan" name="housingLoan" min="0" step="0.01">
                                    <small class="form-text text-muted">每月1000元</small>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editHousingRent">住房租金（每月）</label>
                                    <input type="number" class="form-control" id="editHousingRent" name="housingRent" min="0" step="0.01">
                                    <small class="form-text text-muted">一线城市每月1500元，二线城市每月1100元，三线城市每月800元</small>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editElderlyCare">赡养老人（每月）</label>
                                    <input type="number" class="form-control" id="editElderlyCare" name="elderlyCare" min="0" step="0.01">
                                    <small class="form-text text-muted">独生子女每月2000元，非独生子女分摊每月2000元</small>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editMedicalExpense">医疗费用（每月）</label>
                                    <input type="number" class="form-control" id="editMedicalExpense" name="medicalExpense" min="0" step="0.01">
                                    <small class="form-text text-muted">年度医疗费用超过15000元的部分，可按每年60000元限额据实扣除</small>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="editChildCare">子女3岁以下（每月）</label>
                                    <input type="number" class="form-control" id="editChildCare" name="childCare" min="0" step="0.01">
                                    <small class="form-text text-muted">每个3岁以下婴幼儿每月1000元</small>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">取消</button>
                    <button type="button" class="btn btn-primary" onclick="updateDeduction()">保存</button>
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
            table = $('#deductionTable').DataTable({
                ajax: {
                    url: '${pageContext.request.contextPath}/deduction/list',
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
                            console.log(employee?.name)
                            return employee ? employee.name : '';
                        }
                    },
                    { data: 'year' },
                    {
                        data: 'childrenEducation',
                        render: function(data) {
                            return data ? data.toFixed(2) : '0.00';
                        }
                    },
                    {
                        data: 'continuingEducation',
                        render: function(data) {
                            return data ? data.toFixed(2) : '0.00';
                        }
                    },
                    {
                        data: 'housingLoan',
                        render: function(data) {
                            return data ? data.toFixed(2) : '0.00';
                        }
                    },
                    {
                        data: 'housingRent',
                        render: function(data) {
                            return data ? data.toFixed(2) : '0.00';
                        }
                    },
                    {
                        data: 'elderlyCare',
                        render: function(data) {
                            return data ? data.toFixed(2) : '0.00';
                        }
                    },
                    {
                        data: 'medicalExpense',
                        render: function(data) {
                            return data ? data.toFixed(2) : '0.00';
                        }
                    },
                    {
                        data: 'childCare',
                        render: function(data) {
                            return data ? data.toFixed(2) : '0.00';
                        }
                    },
                    {
                        data: null,
                        render: function(data, type, row) {
                            return '<div class="btn-group btn-group-sm">' +
                                '<button type="button" class="btn btn-primary" onclick="editDeduction(' + row.id + ')"><i class="fas fa-edit"></i> 编辑</button>' +
                                '<button type="button" class="btn btn-danger" onclick="deleteDeduction(' + row.id + ')"><i class="fas fa-trash"></i> 删除</button>' +
                                '</div>';
                        }
                    }
                ],
                language: {

                }
            });

            // 员工筛选
            $('#employeeFilter').change(function() {
                filterTable();
            });

            // 年份筛选
            $('#yearFilter').change(function() {
                filterTable();
            });
        });

        // 筛选表格
        function filterTable() {
            const employeeId = $('#employeeFilter').val();
            const year = $('#yearFilter').val();

            let url = '${pageContext.request.contextPath}/deduction/list?';
            if (employeeId) {
                url += 'employeeId=' + employeeId + '&';
            }
            if (year) {
                url += 'year=' + year;
            }

            table.ajax.url(url).load();
        }

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
                        let filterOptions = '<option value="">全部员工</option>';

                        employees.forEach(function(emp) {
                            if (emp.status === 1) { // 只显示在职员工
                                options += '<option value="' + emp.id + '">' + emp.name + ' (' + emp.employeeNo + ')</option>';
                                filterOptions += '<option value="' + emp.id + '">' + emp.name + ' (' + emp.employeeNo + ')</option>';
                            }
                        });

                        $('#employeeId').html(options);
                        $('#employeeFilter').html(filterOptions);
                    }
                }
            });
        }

        // 打开新增专项附加扣除模态框
        function openAddModal() {
            $('#addDeductionForm')[0].reset();
            $('#addDeductionModal').modal('show');
        }

        // 保存专项附加扣除
        function saveDeduction() {
            const formData = $('#addDeductionForm').serialize();

            $.ajax({
                url: '${pageContext.request.contextPath}/deduction/add',
                type: 'POST',
                data: formData,
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        $('#addDeductionModal').modal('hide');
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

        // 编辑专项附加扣除
        function editDeduction(id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/deduction/edit/' + id,
                type: 'GET',
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        const deduction = res.data;

                        $('#editId').val(deduction.id);
                        $('#editEmployeeName').val(deduction.employeeId);
                        $('#editYear').val(deduction.year + '年');
                        $('#editChildrenEducation').val(deduction.childrenEducation);
                        $('#editContinuingEducation').val(deduction.continuingEducation);
                        $('#editHousingLoan').val(deduction.housingLoan);
                        $('#editHousingRent').val(deduction.housingRent);
                        $('#editElderlyCare').val(deduction.elderlyCare);
                        $('#editMedicalExpense').val(deduction.medicalExpense);
                        $('#editChildCare').val(deduction.childCare);

                        $('#editDeductionModal').modal('show');
                    } else {
                        alert(res.message);
                    }
                },
                error: function() {
                    alert('系统错误，请稍后再试');
                }
            });
        }

        // 更新专项附加扣除
        function updateDeduction() {
            const formData = $('#editDeductionForm').serialize();

            $.ajax({
                url: '${pageContext.request.contextPath}/deduction/edit',
                type: 'POST',
                data: formData,
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        $('#editDeductionModal').modal('hide');
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

        // 删除专项附加扣除
        function deleteDeduction(id) {
            if (confirm('确定要删除该专项附加扣除吗？')) {
                $.ajax({
                    url: '${pageContext.request.contextPath}/deduction/delete/' + id,
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
