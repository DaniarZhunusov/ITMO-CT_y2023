<#-- @ftlvariable name="user" type="ru.itmo.wp.domain.User" -->
<#import "commons.ftlh" as c>

<@c.page>
    <#if user??>
        <section class = "user_main">
            <div>
                <div class="user">${user.login}</div>
                <ul>
                    <li>id: ${user.id}</li>
                    <li>login: ${user.login}</li>
                    <li>creation time: ${user.creationTime}</li>
                </ul>
            </div>
        </section>
    <#else>
        No such user
    </#if>
</@c.page>