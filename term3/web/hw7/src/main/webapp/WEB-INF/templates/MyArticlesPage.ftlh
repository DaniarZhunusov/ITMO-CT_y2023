<#-- @ftlvariable name="articles" type="ru.itmo.wp.model.domain.Article[]" -->
<#-- @ftlvariable name="article" type="ru.itmo.wp.model.domain.Article" -->
<#import "commons.ftlh" as c>

<@c.page>
    <div class="datatable">
        <div class="caption">Article</div>
        <table>
            <thead>
            <tr>
                <th>Id</th>
                <th>Title</th>
                <th>Change status</th>
            </tr>
            </thead>
            <tbody>
            <#list articles as article>
                <tr>
                    <td class="id" id="${article.id}">${article.id}</td>
                    <td>${article.title}</td>
                    <#assign cond = condition(article)/>
                    <td class="post-hidden-changer">
                        <button name="${cond}">
                            ${cond}
                        </button>
                    </td>
                </tr>
            </#list>
            </tbody>
        </table>
    </div>
    <script>
        $(function () {
            $(".post-hidden-changer button").click(function () {
                    const articleId = $(this).closest("tr").find(".id").attr("id");
                    const button = $(this)
                    const success = function (response) {
                         if (response["hidden"]) {
                            button.text("show")
                            button.attr("name", "show")
                        } else {
                            button.text("hide")
                            button.attr("name", "hide")
                        }
                    }
                    ajax({
                        action:
                            button.attr("name"),
                        articleId: articleId
                    }, success)
                }
            )
        })
    </script>
</@c.page>

<#function condition article>
    <#if article.hidden>
        <#return "show">
    <#else>
        <#return "hide">
    </#if>
</#function>