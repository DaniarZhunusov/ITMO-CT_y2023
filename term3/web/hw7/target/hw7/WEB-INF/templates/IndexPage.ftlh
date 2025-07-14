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
                <img class="post-image" src="" alt="Post image"/>
                <div class="body"></div>
                <div class="footer">
                    <div class="left">
                        <img src="img/voteup.png" title="Vote Up" alt="Vote Up"/>
                        <span class="positive-score">+173</span>
                        <img src="img/votedown.png" title="Vote Down" alt="Vote Down"/>
                    </div>
                    <div class="right">
                        <img src="img/comments_16x16.png" title="Comments" alt="Comments"/>
                        <a href="#">68</a>
                    </div>
                </div>
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

                    if (article["imageUrl"]) {
                        $post.find(".post-image").attr("src", article["imageUrl"]).show();
                    } else {
                        $post.find(".post-image").hide();
                    }

                    $(".posts").append($post);
                });
            };

            ajax({action: "findAllPosts"}, success);
        });
    </script>
</@c.page>
