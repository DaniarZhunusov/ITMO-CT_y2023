<#-- @ftlvariable name="post" type="ru.itmo.wp.domain.Post" -->
<#-- @ftlvariable name="comment" type="ru.itmo.wp.domain.Comment" -->
<#import "commons.ftlh" as c>
<#import "/spring.ftl" as spring/>

<@c.page>
    <#if post??>
        <@c.post post=post/>
        <#if user??>
            <div class="form">
                <div class="header">Write comment</div>
                <div class="body">
                    <form method="post" action="/post/${post.id}/comment" >
                        <div class="field">
                            <div class="value">
                                <label>
                                    <textarea name="text" value="${comment.text!}"></textarea>
                                </label>
                            </div>
                            <@c.error "comment.text"/>
                        </div>
                        <div class="button-field">
                            <input type="submit" value="Write">
                        </div>
                    </form>
                </div>
            </div>
        </#if>
        <#if post.comments?has_content>
            <div class="comments">
                <div class="caption">
                    <img src="<@spring.url '/img/comments_16x16.png'/>" title="Comments" alt="Comments"/>
                    Comments (${post.comments?size})
                </div>
                <table class="comment-table">
                    <#list post.comments as cmnt>
                        <tr class="comment">
                            <td class="login">${cmnt.user.login}:</td>
                            <td class="text">${cmnt.text}</td>
                        </tr>
                    </#list>
                </table>
            </div>
        </#if>
    <#else >
        No such post
    </#if>
</@c.page>