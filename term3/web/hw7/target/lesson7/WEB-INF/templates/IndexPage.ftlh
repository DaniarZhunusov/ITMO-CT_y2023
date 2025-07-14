<#import "commons.ftlh" as c>

<@c.page>
    <div class="posts">
        <div class="noData">
            <div>
                No data
            </div>
        </div>

        <template>
            <article>
                <div class="title"></div>
                <div class="information"></div>
                <div class="body"></div>
            </article>
        </template>
    </div>
    <script>
        $(function () {
            const $noData = $(".posts .noData");
            $noData.find("div").text("Loading...");

            const success = function (response) {
                if (response["articles"]) {
                    $noData.hide();
                }
                response["articles"].forEach(function (article) {
                    const $post = $($(".posts template").prop("content")).clone();
                    $post.find(".title").text(article["title"]);
                    $post.find(".information").text("By " + article["userName"] + ", at " + article["creationTime"]);
                    $post.find(".body").text(article["text"]);
                    $(".posts").append($post);
                });
            }

            ajax({action: "findAllPosts"}, success)

        })
    </script>
</@c.page>
