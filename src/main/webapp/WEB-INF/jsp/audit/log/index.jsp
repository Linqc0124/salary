<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>审计日志</title>
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
                <h5>审计日志</h5>
            </div>
            <div class="card-body">
                <div class="row mb-3">
                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="usernameFilter">用户名</label>
                            <input type="text" class="form-control" id="usernameFilter" placeholder="输入用户名">
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="operationFilter">操作类型</label>
                            <select id="operationFilter" class="form-control">
                                <option value="">全部操作</option>
                                <option value="登录">登录</option>
                                <option value="登出">登出</option>
                                <option value="新增">新增</option>
                                <option value="修改">修改</option>
                                <option value="删除">删除</option>
                                <option value="查询">查询</option>
                            </select>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="statusFilter">状态</label>
                            <select id="statusFilter" class="form-control">
                                <option value="">全部状态</option>
                                <option value="1">成功</option>
                                <option value="0">失败</option>
                            </select>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="form-group">
                            <label for="dateFilter">日期</label>
                            <input type="date" class="form-control" id="dateFilter">
                        </div>
                    </div>
                </div>
                <div class="row mb-3">
                    <div class="col-md-12">
                        <button type="button" class="btn btn-primary" onclick="searchLogs()">
                            <i class="fas fa-search"></i> 查询
                        </button>
                        <button type="button" class="btn btn-secondary" onclick="resetFilters()">
                            <i class="fas fa-redo"></i> 重置
                        </button>
                        <button type="button" class="btn btn-success float-right" onclick="exportLogs()">
                            <i class="fas fa-file-export"></i> 导出日志
                        </button>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="auditLogTable" class="table table-striped table-bordered">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>用户名</th>
                                <th>操作类型</th>
                                <th>请求方法</th>
                                <th>IP地址</th>
                                <th>操作时间</th>
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

    <!-- 查看审计日志详情模态框 -->
    <div class="modal fade" id="viewAuditLogModal" tabindex="-1" role="dialog" aria-labelledby="viewAuditLogModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="viewAuditLogModalLabel">审计日志详情</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6">
                            <p><strong>用户名：</strong><span id="viewUsername"></span></p>
                            <p><strong>操作类型：</strong><span id="viewOperation"></span></p>
                            <p><strong>请求方法：</strong><span id="viewMethod"></span></p>
                            <p><strong>IP地址：</strong><span id="viewIp"></span></p>
                        </div>
                        <div class="col-md-6">
                            <p><strong>操作时间：</strong><span id="viewCreateTime"></span></p>
                            <p><strong>状态：</strong><span id="viewStatus"></span></p>
                            <p><strong>HMAC校验：</strong><span id="viewHmac"></span></p>
                        </div>
                    </div>
                    <hr>
                    <h6>请求参数</h6>
                    <pre id="viewParams" class="bg-light p-2" style="max-height: 200px; overflow-y: auto;"></pre>
                    <hr>
                    <h6>操作消息</h6>
                    <pre id="viewMessage" class="bg-light p-2" style="max-height: 200px; overflow-y: auto;"></pre>
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

        $(function() {
            // 初始化DataTable
            table = $('#auditLogTable').DataTable({
                ajax: {
                    url: '${pageContext.request.contextPath}/audit/log/list',
                    dataSrc: function(json) {
                        return json.data.records;
                    }
                },
                columns: [
                    { data: 'id' },
                    { data: 'username' },
                    { data: 'operation' },
                    { data: 'method' },
                    { data: 'ip' },
                    {
                        data: 'createTime',
                        render: function(data) {
                            return new Date(data).toLocaleString();
                        }
                    },
                    {
                        data: 'status',
                        render: function(data) {
                            return data === 1 ?
                                '<span class="badge badge-success">成功</span>' :
                                '<span class="badge badge-danger">失败</span>';
                        }
                    },
                    {
                        data: null,
                        render: function(data, type, row) {
                            return '<div class="btn-group btn-group-sm">' +
                                '<button type="button" class="btn btn-info" onclick="viewAuditLog(' + row.id + ')"><i class="fas fa-eye"></i> 查看</button>' +
                                '</div>';
                        }
                    }
                ],
                order: [[5, 'desc']],
                language: {

                }
            });
        });

        // 查询日志
        function searchLogs() {
            const username = $('#usernameFilter').val();
            const operation = $('#operationFilter').val();
            const status = $('#statusFilter').val();
            const date = $('#dateFilter').val();

            let url = '${pageContext.request.contextPath}/audit/log/list?';
            if (username) {
                url += 'username=' + encodeURIComponent(username) + '&';
            }
            if (operation) {
                url += 'operation=' + encodeURIComponent(operation) + '&';
            }
            if (status) {
                url += 'status=' + status + '&';
            }
            if (date) {
                url += 'date=' + date;
            }

            table.ajax.url(url).load();
        }

        // 重置筛选条件
        function resetFilters() {
            $('#usernameFilter').val('');
            $('#operationFilter').val('');
            $('#statusFilter').val('');
            $('#dateFilter').val('');

            table.ajax.url('${pageContext.request.contextPath}/audit/log/list').load();
        }

        // 导出日志
        function exportLogs() {
            const username = $('#usernameFilter').val();
            const operation = $('#operationFilter').val();
            const status = $('#statusFilter').val();
            const date = $('#dateFilter').val();

            let url = '${pageContext.request.contextPath}/audit/log/export?';
            if (username) {
                url += 'username=' + encodeURIComponent(username) + '&';
            }
            if (operation) {
                url += 'operation=' + encodeURIComponent(operation) + '&';
            }
            if (status) {
                url += 'status=' + status + '&';
            }
            if (date) {
                url += 'date=' + date;
            }

            window.location.href = url;
        }

        // 查看审计日志详情
        function viewAuditLog(id) {
            $.ajax({
                url: '${pageContext.request.contextPath}/audit/log/detail/' + id,
                type: 'GET',
                dataType: 'json',
                success: function(res) {
                    if (res.code === 200) {
                        const log = res.data;

                        $('#viewUsername').text(log.username || '未登录');
                        $('#viewOperation').text(log.operation);
                        $('#viewMethod').text(log.method || '');
                        $('#viewIp').text(log.ip);
                        $('#viewCreateTime').text(new Date(log.createTime).toLocaleString());
                        $('#viewStatus').text(log.status === 1 ? '成功' : '失败');
                        $('#viewHmac').html(log.hmac ? '<span class="text-success">有效</span>' : '<span class="text-danger">无效</span>');

                        try {
                            if (log.params) {
                                const params = JSON.parse(log.params);
                                $('#viewParams').text(JSON.stringify(params, null, 2));
                            } else {
                                $('#viewParams').text('无参数');
                            }
                        } catch (e) {
                            $('#viewParams').text(log.params || '无参数');
                        }

                        $('#viewMessage').text(log.message || '无消息');

                        $('#viewAuditLogModal').modal('show');
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
