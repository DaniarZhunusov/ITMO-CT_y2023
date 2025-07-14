<#-- @ftlvariable name="userColors" type="ru.itmo.web.hw4.model.UserColor[]" -->
<#-- @ftlvariable name="uri" type="String" -->


<#macro page>
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="UTF-8">
        <title>Codeforces</title>
        <link rel="stylesheet" href="/css/normalize.css">
        <link rel="stylesheet" href="/css/style.css">
        <link rel="icon" href="/favicon.ico">
    </head>
    <body>
    <@header/>
    <div class="middle">
        <@sidebar/>
        <main>
            <#nested/>
        </main>
    </div>
    <@footer/>
    </body>
    </html>
</#macro>

<#macro header>
    <header>
        <a href="#" class="logo"><img alt="Codeforces" title="Codeforces"
                                      src="/img/codeforces-logo-with-telegram.png"></a>
        <div class="languages">
            <a href="#"><img alt="In English" title="In English"
                             src="/img/gb.png"></a>
            <a href="#"><img alt="In Russian" title="In Russian"
                             src="/img/ru.png"></a>
        </div>
        <div class="enter-or-register-box">
            <#if user??>
                <@userlink user "false"/>
                |
                <a href="/logout">Logout</a>
            <#else>
                <a href="/enter">Enter</a>
                |
                <a href="/register">Register</a>
            </#if>
        </div>
        <nav>
            <#assign items=[
            {"href": "/index", "name": "Home"},
            {"href": "/misc/help", "name": "Help"},
            {"href": "/contests", "name": "Contests"},
            {"href": "/users", "name": "Users"}
            ]/>
            <ul>
                <#list items as item>
                    <li>
                        <#if item["href"]?starts_with(uri)>
                            <a class="underlinedlink" href="${item.href}">${item.name}</a>
                        <#else>
                            <a href="${item.href}">${item.name}</a>
                        </#if>
                    </li>
                </#list>
            </ul>
        </nav>
    </header>
</#macro>

    /* show_posts short place */
<#macro sidebar>
    <aside>
        <@show_posts "true" "sibebar"/>
    </aside>
</#macro>

<#macro footer>
    <footer>
        2010-2020 &copy; by Mike Mirzayanov
    </footer>
</#macro>

<#function getColor user>
    <#list userColors as userColor>
        <#if userColor == user.userColor>
            <#return userColor.toCssClass()>
        </#if>
    </#list>
</#function>

<#macro userlink user nameOnly>
    <#if nameOnly == "true">
        <a class="userLink"
           href="/user?handle=${user.handle}">${user.handle}</a>
    <#else>
        <a class="userLink userLink${getColor(user)}" href="/user?handle=${user.handle}">${user.handle}</a>
    </#if>
</#macro>

<#function findBy items key value>
    <#list items as item>
        <#if item[key] == value>
            <#return item/>
        </#if>
    </#list>
</#function>

<#macro userprofile u>
    <#if u?has_content>
        <div class="userbox">
            <img class="userimg" src="../../img/no-title.jpg">
            <header>
                <a href="#">${u.name}</a>
            </header>
            <ul>
                <li>id: ${u.id}</li>
                <li>handle: ${u.handle}</li>
                <li>name: ${u.name}</li>
            </ul>

        </div>

        <div class="usernav">
            <#list users as cur>
                <#if cur.id == u.id>
                    <#if !(cur?is_first)>
                        <a href="/user?handle=${users[cur_index - 1].handle}">&larr;</a>
                    <#else>
                        &larr;
                    </#if>
                    <#if cur?has_next>
                        <a href="/user?handle=${users[cur_index + 1].handle}">&rarr;</a>
                    <#else>
                        &rarr;
                    </#if>
                    <#break>
                </#if>
            </#list>
        </div>
        <div class="user_posts">
            <a href="/posts?user_id=${u.id}">Posts</a>
            by this user: <@user_posts_count u/>
        </div>
    <#else>
        No such user.
    </#if>
</#macro>

<#macro show_post_text post short>
    <#if short == "true" && (post.text?length > 350)>
            ${post.text?substring(0, 351) + "..."}
    <#else>
        ${post.text}
    </#if>
</#macro>

<#macro sidebar_markup p short>
    <section>
        <div class="header">Post #${p.id}</div>
        <div class="body">
            <@show_post_text p short/>
        </div>
        <div class="footer">
            <a href="/post?post_id=${p.id}">View all</a>
        </div>
    </section>
</#macro>

<#macro middle_markup p short>
    <article>
        <#assign pi = findBy(users, "id", p.userId)!/>
        <div class="title">${p.title}</div>
        <div class="information">by <a class="userLink userLink${getColor(pi)}">${pi.name}</a></div>
        <div class="body">
            <@show_post_text p short/>
        </div>
        <ul class="attachment">
            <li>Codeforces post attachment</li>
        </ul>
        <div class="footer">
            <div class="left">
                <img src="../../img/voteup.png" title="Vote Up" alt="Vote Up"/>
                <span class="positive-score">+173</span>
                <img src="../../img/votedown.png" title="Vote Down" alt="Vote Down"/>
            </div>
            <div class="right">
                <img src="../../img/date_16x16.png" title="Publish Time" alt="Publish Time"/>
                1 days ago
                <img src="../../img/comments_16x16.png" title="Comments" alt="Comments"/>
                <a href="#">68</a>
            </div>
        </div>
    </article>
</#macro>

<#macro show_post p short place>
    <#if p?has_content>
    <#assign pi = findBy(users, "id", p.userId)!/>
    <#if place == "sibebar">
        <@sidebar_markup p short/>
    <#elseif place == "middle">
        <@middle_markup p short/>
    </#if>
    <#else>
        Not found any posts by this post_id
    </#if>
</#macro>

<#macro show_posts short place>
    <#list posts?size-1..0 as i>
        <@show_post posts[i] short place/>
    </#list>
</#macro>

<#macro user_posts_count u>
    <#assign count = 0>
    <#list posts as post>
        <#if post.userId == u.id>
            <#assign count++>
        </#if>
    </#list>
    ${count}
</#macro>

<#macro show_posts_user_id uid short place>
    <#assign flag_any_posts = 0>
    <#list posts?size-1..0 as i>
        <#if posts[i].userId == uid>
            <@show_post posts[i] short place/>
            <#assign flag_any_posts++>
        </#if>
    </#list>
    <#if flag_any_posts == 0>
        Not found any posts by this user_id
    </#if>
</#macro>