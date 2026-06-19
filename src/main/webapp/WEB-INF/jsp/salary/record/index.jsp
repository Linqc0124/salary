<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>工资记录</title>
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
                <h5>工资记录查询</h5>
            </div>
            <div class="card-body">
                <div class="row mb-3">
                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="employeeFilter">员工</label>
                            <select id="employeeFilter" class="form-control">
                                <option value="">全部员工</option>
                                <!-- 员工选项将通过Ajax加载 -->
                            </select>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="departmentFilter">部门</label>
                            <select id="departmentFilter" class="form-control">
                                <option value="">全部部门</option>
                                <!-- 部门选项将通过Ajax加载 -->
                            </select>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="yearFilter">年份</label>
                            <select id="yearFilter" class="form-control">
                                <option value="">全部年份</option>
                                <option value="2023">2023年</option>
                                <option value="2024">2024年</option>
                                <option value="2025">2025年</option>
                            </select>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="monthFilter">月份</label>
                            <select id="monthFilter" class="form-control">
                                <option value="">全部月份</option>
                                <option value="1">1月</option>
                                <option value="2">2月</option>
                                <option value="3">3月</option>
                                <option value="4">4月</option>
                                <option value="5">5月</option>
                                <option value="6">6月</option>
                                <option value="7">7月</option>
                                <option value="8">8月</option>
                                <option value="9">9月</option>
                                <option value="10">10月</option>
                                <option value="11">11月</option>
                                <option value="12">12月</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="salaryRecordTable" class="table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>员工</th>
                                <th>部门</th>
                                <th>年月</th>
                                <th>应发工资</th>
                                <th>社保</th>
                                <th>公积金</th>
                                <th>个税</th>
                                <th>实发工资</th>
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

    <!-- 查看工资记录详情模态框 -->
    <div class="modal fade" id="viewSalaryRecordModal" tabindex="-1" role="dialog" aria-labelledby="viewSalaryRecordModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="viewSalaryRecordModalLabel">工资条详情</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>员工：</strong><span id="viewEmployeeName"></span></p>
                            <p><strong>部门：</strong><span id="viewDepartmentName"></span></p>
                            <p><strong>年月：</strong><span id="viewYearMonth"></span></p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>状态：</strong><span id="viewStatus"></span></p>
                            <p><strong>创建时间：</strong><span id="viewCreateTime"></span></p>
                        </div>
                    </div>

                    <hr>
                    <h6>收入项目</h6>
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>基本工资：</strong><span id="viewBaseSalary"></span></p>
                            <p><strong>岗位工资：</strong><span id="viewPositionSalary"></span></p>
                            <p><strong>午餐补贴：</strong><span id="viewLunchSubsidy"></span></p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>全勤奖：</strong><span id="viewFullAttendanceBonus"></span></p>
                            <p><strong>加班工资：</strong><span id="viewOvertimeSalary"></span></p>
                            <p><strong>其他奖金：</strong><span id="viewOtherBonus"></span></p>
                        </div>
                    </div>
                    <p><strong>应发工资：</strong><span id="viewGrossSalary"></span></p>

                    <hr>
                    <h6>扣除项目</h6>
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>社保：</strong><span id="viewSocialInsurance"></span></p>
                            <p><strong>公积金：</strong><span id="viewHousingFund"></span></p>
                            <p><strong>个人所得税：</strong><span id="viewTaxAmount"></span></p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>缺勤扣款：</strong><span id="viewAbsenceDeduction"></span></p>
                            <p><strong>其他扣款：</strong><span id="viewOtherDeduction"></span></p>
                            <p><strong>专项附加扣除：</strong><span id="viewSpecialDeduction"></span></p>
                        </div>
                    </div>

                    <hr>
                    <h5 class="text-primary"><strong>实发工资：</strong><span id="viewNetSalary"></span></h5>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" onclick="printSalaryRecord()">打印工资条</button>
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
        let departments = [];

        $(function() {
            // 加载员工和部门数据
            loadEmployees();
            loadDepartments();

            // 初始化DataTable
            setTimeout(()=> {
                table = $('#salaryRecordTable').DataTable({
                    ajax: {
                        url: '${pageContext.request.contextPath}/salary/record/list',
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
                            data: 'departmentId',
                            render: function(data) {
                                const department = departments.find(d => d.id === data);
                                return department ? department.name : '';
                            }
                        },
                        {
                            data: null,
                            render: function(data) {
                                return data.year + '年' + data.month + '月';
                            }
                        },
                        {
                            data: 'grossSalary',
                            render: function(data) {
                                return data ? data.toFixed(2) : '0.00';
                            }
                        },
                        {
                            data: 'socialInsurance',
                            render: function(data) {
                                return data ? data.toFixed(2) : '0.00';
                            }
                        },
                        {
                            data: 'housingFund',
                            render: function(data) {
                                return data ? data.toFixed(2) : '0.00';
                            }
                        },
                        {
                            data: 'taxAmount',
                            render: function(data) {
                                return data ? data.toFixed(2) : '0.00';
                            }
                        },
                        {
                            data: 'netSalary',
                            render: function(data) {
                                return data ? data.toFixed(2) : '0.00';
                            }
                        },
                        {
                            data: 'status',
                            render: function(data) {
                                return data === 1 ?
                                    '<span class="badge badge-success">有效</span>' :
                                    '<span class="badge badge-danger">作废</span>';
                            }
                        },
                        {
                            data: null,
                            render: function(data, type, row) {
                                return '<div class="btn-group btn-group-sm">' +
                                    '<button type="button" class="btn btn-info" onclick="viewSalaryRecord(' + row.id + ')"><i class="fas fa-eye"></i> 查看</button>' +
                                    '</div>';
                            }
                        }
                    ],
                    order: [[3, 'desc']],
                    language: {

                    }
                });
            },400)

            // 员工筛选
            $('#employeeFilter').change(function() {
                filterTable();
            });

            // 部门筛选
            $('#departmentFilter').change(function() {
                filterTable();
            });

            // 年份筛选
            $('#yearFilter').change(function() {
                filterTable();
            });

            // 月份筛选
            $('#monthFilter').change(function() {
                filterTable();
            });
        });

        // 筛选表格
        function filterTable() {
            const employeeId = $('#employeeFilter').val();
            const departmentId = $('#departmentFilter').val();
            const year = $('#yearFilter').val();
            const month = $('#monthFilter').val();

            let url = '${pageContext.request.contextPath}/salary/record/list?';
            if (employeeId) {
                url += 'employeeId=' + employeeId + '&';
            }
            if (departmentId) {
                url += 'departmentId=' + departmentId + '&';
            }
            if (year) {
                url += 'year=' + year + '&';
            }
            if (month) {
                url += 'month=' + month;
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
                        let options = '<option value="">全部员工</option>';

                        employees.forEach(function(emp) {
                            options += '<option value="' + emp.id + '">' + emp.name + ' (' + emp.employeeNo + ')</option>';
                        });

                        $('#employeeFilter').html(options);
                    }
                }
            });
        }

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
                        let options = '<option value="">全部部门</option>';

                        departments.forEach(function(dept) {
                            options += '<option value="' + dept.id + '">' + dept.name + '</option>';
                        });

                        $('#departmentFilter').html(options);
                    }
                }
            });
        }

        // 查看工资记录详情
        function viewSalaryRecord(id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/salary/record/detail/' + id,
                type: 'GET',
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        const salaryRecord = res.data;

                        $('#viewYearMonth').text(salaryRecord.year + '年' + salaryRecord.month + '月');
                        $('#viewStatus').text(salaryRecord.status === 1 ? '有效' : '作废');
                        $('#viewCreateTime').text(new Date(salaryRecord.createTime).toLocaleString());
                        $('#viewEmployeeName').text(salaryRecord.employeeName);
                        $('#viewDepartmentName').text(salaryRecord.departmentName);

                        $('#viewBaseSalary').text(formatMoney(salaryRecord.baseSalary));
                        $('#viewPositionSalary').text(formatMoney(salaryRecord.positionSalary));
                        $('#viewLunchSubsidy').text(formatMoney(salaryRecord.lunchSubsidy));
                        $('#viewFullAttendanceBonus').text(formatMoney(salaryRecord.fullAttendanceBonus));
                        $('#viewOvertimeSalary').text(formatMoney(salaryRecord.overtimeSalary));
                        $('#viewOtherBonus').text(formatMoney(salaryRecord.otherBonus));
                        $('#viewGrossSalary').text(formatMoney(salaryRecord.grossSalary));

                        $('#viewSocialInsurance').text(formatMoney(salaryRecord.socialInsurance));
                        $('#viewHousingFund').text(formatMoney(salaryRecord.housingFund));
                        $('#viewTaxAmount').text(formatMoney(salaryRecord.taxAmount));
                        $('#viewAbsenceDeduction').text(formatMoney(salaryRecord.absenceDeduction));
                        $('#viewOtherDeduction').text(formatMoney(salaryRecord.otherDeduction));
                        $('#viewSpecialDeduction').text(formatMoney(salaryRecord.specialDeduction));

                        $('#viewNetSalary').text(formatMoney(salaryRecord.netSalary));

                        $('#viewSalaryRecordModal').modal('show');
                    } else {
                        alert(res.message);
                    }
                },
                error: function() {
                    alert('系统错误，请稍后再试');
                }
            });
        }

        // 格式化金额
        function formatMoney(amount) {
            if (amount === null || amount === undefined) {
                return '¥0.00';
            }
            return '¥' + parseFloat(amount).toFixed(2);
        }

        // 打印工资条
        // 修改打印工资条函数，解决中文乱码和EL表达式问题
        function printSalaryRecord() {
            const printWindow = window.open('', '_blank');
            const employeeName = $('#viewEmployeeName').text();
            const yearMonth = $('#viewYearMonth').text();
            const departmentName = $('#viewDepartmentName').text();

            // 获取各项工资数据
            const baseSalary = $('#viewBaseSalary').text();
            const positionSalary = $('#viewPositionSalary').text();
            const lunchSubsidy = $('#viewLunchSubsidy').text();
            const fullAttendanceBonus = $('#viewFullAttendanceBonus').text();
            const overtimeSalary = $('#viewOvertimeSalary').text();
            const otherBonus = $('#viewOtherBonus').text();
            const grossSalary = $('#viewGrossSalary').text();

            // 获取各项扣除数据
            const socialInsurance = $('#viewSocialInsurance').text();
            const housingFund = $('#viewHousingFund').text();
            const taxAmount = $('#viewTaxAmount').text();
            const absenceDeduction = $('#viewAbsenceDeduction').text();
            const otherDeduction = $('#viewOtherDeduction').text();
            const specialDeduction = $('#viewSpecialDeduction').text();
            const netSalary = $('#viewNetSalary').text();

            // 构建HTML内容，避免使用模板字符串中的EL表达式
            let htmlContent = '<!DOCTYPE html>';
            htmlContent += '<html>';
            htmlContent += '<head>';
            htmlContent += '<meta charset="UTF-8">';
            htmlContent += '<title>工资条 - ' + employeeName + ' - ' + yearMonth + '</title>';
            htmlContent += '<style>';
            htmlContent += 'body { font-family: Arial, sans-serif; margin: 20px; }';
            htmlContent += '.header { text-align: center; margin-bottom: 20px; }';
            htmlContent += '.salary-table { width: 100%; border-collapse: collapse; }';
            htmlContent += '.salary-table th, .salary-table td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }';
            htmlContent += '.total { font-weight: bold; margin-top: 20px; }';
            htmlContent += '</style>';
            htmlContent += '</head>';
            htmlContent += '<body>';
            htmlContent += '<div class="header">';
            htmlContent += '<h2>工资条</h2>';
            htmlContent += '<p>员工：' + employeeName + ' | 部门：' + departmentName + ' | 时间：' + yearMonth + '</p>';
            htmlContent += '</div>';

            htmlContent += '<h3>收入项目</h3>';
            htmlContent += '<table class="salary-table">';
            htmlContent += '<tr>';
            htmlContent += '<th>项目</th>';
            htmlContent += '<th>金额</th>';
            htmlContent += '<th>项目</th>';
            htmlContent += '<th>金额</th>';
            htmlContent += '</tr>';
            htmlContent += '<tr>';
            htmlContent += '<td>基本工资</td>';
            htmlContent += '<td>' + baseSalary + '</td>';
            htmlContent += '<td>岗位工资</td>';
            htmlContent += '<td>' + positionSalary + '</td>';
            htmlContent += '</tr>';
            htmlContent += '<tr>';
            htmlContent += '<td>午餐补贴</td>';
            htmlContent += '<td>' + lunchSubsidy + '</td>';
            htmlContent += '<td>全勤奖</td>';
            htmlContent += '<td>' + fullAttendanceBonus + '</td>';
            htmlContent += '</tr>';
            htmlContent += '<tr>';
            htmlContent += '<td>加班工资</td>';
            htmlContent += '<td>' + overtimeSalary + '</td>';
            htmlContent += '<td>其他奖金</td>';
            htmlContent += '<td>' + otherBonus + '</td>';
            htmlContent += '</tr>';
            htmlContent += '</table>';

            htmlContent += '<p><strong>应发工资：' + grossSalary + '</strong></p>';

            htmlContent += '<h3>扣除项目</h3>';
            htmlContent += '<table class="salary-table">';
            htmlContent += '<tr>';
            htmlContent += '<th>项目</th>';
            htmlContent += '<th>金额</th>';
            htmlContent += '<th>项目</th>';
            htmlContent += '<th>金额</th>';
            htmlContent += '</tr>';
            htmlContent += '<tr>';
            htmlContent += '<td>社保</td>';
            htmlContent += '<td>' + socialInsurance + '</td>';
            htmlContent += '<td>公积金</td>';
            htmlContent += '<td>' + housingFund + '</td>';
            htmlContent += '</tr>';
            htmlContent += '<tr>';
            htmlContent += '<td>个人所得税</td>';
            htmlContent += '<td>' + taxAmount + '</td>';
            htmlContent += '<td>缺勤扣款</td>';
            htmlContent += '<td>' + absenceDeduction + '</td>';
            htmlContent += '</tr>';
            htmlContent += '<tr>';
            htmlContent += '<td>其他扣款</td>';
            htmlContent += '<td>' + otherDeduction + '</td>';
            htmlContent += '<td>专项附加扣除</td>';
            htmlContent += '<td>' + specialDeduction + '</td>';
            htmlContent += '</tr>';
            htmlContent += '</table>';

            htmlContent += '<p class="total">实发工资：' + netSalary + '</p>';

            htmlContent += '<p style="margin-top: 50px; text-align: right;">';
            htmlContent += '打印时间：' + new Date().toLocaleString();
            htmlContent += '</p>';
            htmlContent += '</body>';
            htmlContent += '</html>';

            // 写入HTML内容到打印窗口
            printWindow.document.write(htmlContent);
            printWindow.document.close();
            printWindow.focus();

            // 延迟打印，确保内容加载完成
            setTimeout(function() {
                printWindow.print();
            }, 500);
        }
    </script>
</body>
</html>
