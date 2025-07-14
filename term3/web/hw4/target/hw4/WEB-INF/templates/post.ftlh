<#-- @ftlvariable name="post_id" type="java.lang.Long" -->

<#import "./common.ftlh" as c>

<@c.page>
    <#if post_id?? && post_id?is_number>
        <#assign post = c.findBy(posts, "id", post_id)!/>
        <@c.show_post post "false" "middle"/>
    <#else>
        No such post.
    </#if>
</@c.page>

