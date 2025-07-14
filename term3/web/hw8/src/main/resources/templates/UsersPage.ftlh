<#import "commons.ftlh" as c>

<@c.page>
    <div class="users datatable">
        <div class="caption">User</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Login</th>
                <th>Created</th>
                <th>Status</th>
            </tr>
            </thead>
            <tbody>
            <#if !users?has_content>
                <tr class="noData">
                    <td colspan="3">
                        No data
                    </td>
                </tr>
            <#else>
                <#list users as u>
                    <tr>
                        <td class="id">${u.id}</td>
                        <td class="login"><a href="/user/${u.id}">${u.login}</a></td>
                        <td class="creationTime">${u.creationTime}</td>
                        <td class="status">
                            <#if user??>
                                <form method="post" action="">
                                    <input type="hidden" name="userId" value="${u.id}"/>
                                    <#if u.disabled>
                                        <button type="submit" name="newStatus" value="${false?c}">Enable</button>
                                    <#else>
                                        <button type="submit" name="newStatus" value="${true?c}">Disable</button>
                                    </#if>
                                </form>
                            <#else >
                                <#if u.disabled>
                                    Disabled
                                <#else >
                                    Enabled
                                </#if>
                            </#if>
                        </td>
                    </tr>
                </#list>
            </#if>
            </tbody>
        </table>
    </div>
</@c.page>