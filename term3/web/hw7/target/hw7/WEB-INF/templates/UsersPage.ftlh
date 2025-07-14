<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#-- @ftlvariable name="user" type="ru.itmo.wp.model.domain.User" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <#if user?? && user.admin>
                    <th>Admin</th>
                    <th>Change Status</th>
                </#if>
            </tr>
            </thead>
            <tbody>
            <template>
                <tr>
                    <td class="user-id"></td>
                    <td class="user_login"></td>
                    <#if user?? && user.admin>
                        <td class="user_admin"></td>
                        <td class="user_change_admin_status">
                            <a href="#"></a>
                        </td>
                    </#if>
                </tr>
            </template>
            <tr class="noData">

                <#if user?? && user.admin>
                    <td colspan="5">
                        No data
                    </td>
                <#else>
                    <td colspan="3">
                        No data
                    </td>
                </#if>
            </tr>
            </tbody>
        </table>
    </div>

    <script>
        $(function () {
            const $noData = $(".users .noData");
            $noData.find("td").text("Loading...");

            const success = function (response) {
                response["users"].forEach(function (user) {
                    $noData.hide();
                    const $tr = $($(".users template").prop("content")).clone();
                    $tr.find(".user-id").text(user["id"]);
                    $tr.find(".user_login").text(user["login"]);
                    $tr.find("tr").attr("data-userId", user["id"]);
                    <#if user?? && user.admin>
                    $tr.find(".user_admin").text(user["admin"]);
                    $tr.find(".user_change_admin_status a")
                        .text(user["admin"] ? "disable" : "enable");
                    </#if>
                    $(".users tbody").append($tr);
                });
                $(".users tr .user_change_admin_status a").click(function () {
                        const button = $(this)
                        const admin = button.closest("tr").find(".user_admin")
                        const data = {
                            action: button.text(),
                            userId: button.closest("tr").find(".user-id").text()
                        }
                        const success = function (response) {
                            if (response["admin"]) {
                                button.text("disable")
                                admin.text("true")
                            } else {
                                button.text("enable")
                                admin.text("false")
                            }
                        }
                        ajax(data, success)
                    }
                )
                $(".users tr").click(function () {
                    const data = {
                        action: "findUser",
                        userId: $(this).attr("data-userId")
                    }
                    const success = function (response) {
                        notify(response["user"].creationTime);
                    }
                    ajax(data, success)
                });
            }

            ajax({action: "findAll"}, success)

        })
    </script>
</@c.page>
