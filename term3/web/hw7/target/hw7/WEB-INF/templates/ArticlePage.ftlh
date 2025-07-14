<#-- @ftlvariable name="users" type="ru.itmo.wp.model.domain.User[]" -->
<#-- @ftlvariable name="error" type="java.lang.String" -->
<#import "commons.ftlh" as c>

<@c.page>


    <div class="text-box" xmlns="http://www.w3.org/1999/html">
        <div class="header">Post article</div>
        <div class="body">
            <form method="post" action="">
                <input type="hidden" name="action" value="create"/>
                <div class="field">
                    <div class="name">
                        <label for="title">Title</label>
                    </div>
                    <div class="value">
                        <textarea class="title" id="title" name="title" type="text"></textarea>
                    </div>
                </div>
                <div class="field">
                    <div class="name">
                        <label for="text">Text</label>
                    </div>
                    <textarea class="text" id="text" name="text" type="text"></textarea>
                </div>

                <div class="field">
                    <div class="name">
                        <label for="hidden">Hide article?</label>
                    </div>
                    <div class="value">
                        <input type="checkbox" name="hidden" id="hidden"/>
                    </div>
                </div>

                <div class="error"></div>
                <div class="button-field">
                    <input type="submit" value="Post">
                </div>
            </form>
        </div>
    </div>
    <script>

        $(function () {
            $(".text-box form").submit(function () {
                const title = $(this).find("textarea[name='title']").val();
                const text = $(this).find("textarea[name='text']").val();
                const hidden = $(this).find("input[name='hidden']").is(':checked');
                const $error = $(this).find(".error");
                const data = {
                    action: "post",
                    title,
                    text,
                    hidden
                }

                ajaxRegisterOrEnter(data, $error)
                return false;
            });
        })
    </script>
</@c.page>

