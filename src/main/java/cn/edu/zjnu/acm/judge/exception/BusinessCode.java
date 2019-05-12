/*
 * Copyright 2017 ZJNU ACM.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.edu.zjnu.acm.judge.exception;

import org.springframework.http.HttpStatus;

/**
 * @author zhanhb
 */
public enum BusinessCode {

    // contests
    NO_CONTESTS("onlinejudge.contest.nocontest", HttpStatus.OK),
    NO_SCHEDULED_CONTESTS("onlinejudge.contest.noschedule", HttpStatus.OK),
    NO_PAST_CONTESTS("onlinejudge.contest.nopast", HttpStatus.OK),
    NO_CURRENT_CONTESTS("onlinejudge.contest.nocurrent", HttpStatus.OK),
    // contest
    CONTEST_STARTED("onlinejudge.contest.started"),
    CONTEST_NOT_STARTED("onlinejudge.contest.not.started"),
    CONTEST_NOT_FOUND("onlinejudge.contest.nosuchcontest", HttpStatus.NOT_FOUND),
    CONTEST_ENDED("onlinejudge.contest.ended"),
    CONTEST_PROBLEM_NOT_FOUND("onlinejudge.contest.problem.notfound", HttpStatus.NOT_FOUND),
    CONTEST_ONLY_SUBMIT("onlinejudge.contest.only.submit"),
    CONTEST_ONLY_REGISTER("onlinejudge.contest.only.register"),
    CONTEST_ONLY_VIEW_SOURCE("onlinejudge.contest.only.view.source"),
    LANGUAGE_NOT_FOUND("onlinejudge.language.notfound"),
    // mail
    MAIL_NOT_FOUND("No such mail", HttpStatus.NOT_FOUND),
    MAIL_INVALID_ACCESS("Sorry, invalid access", HttpStatus.FORBIDDEN),
    MAIL_CONTENT_LONG("Sorry, content too long", HttpStatus.PAYLOAD_TOO_LARGE),
    // message
    MESSAGE_NOT_FOUND("No such message", HttpStatus.NOT_FOUND),
    MESSAGE_NO_SUCH_PARENT("onlinejudge.message.no.such.parent"),
    MESSAGE_EMPTY_TITLE("Title can't be blank"),
    // problem
    PROBLEM_NOT_FOUND("onlinejudge.problem.notfound", HttpStatus.NOT_FOUND),
    PROBLEM_SEARCH_KEY_EMPTY("onlinejudge.problem.search.empty"),
    // submission
    SUBMISSION_NOT_FOUND("onlinejudge.submission.notfound", HttpStatus.NOT_FOUND),
    VIEW_SOURCE_PERMISSION_DENIED("onlinejudge.submission.view.denied", HttpStatus.FORBIDDEN),
    SOURCE_CODE_LONG("onlinejudge.submission.sourcecode.long", HttpStatus.PAYLOAD_TOO_LARGE),
    SOURCE_CODE_SHORT("onlinejudge.submission.sourcecode.short"),
    SUBMISSION_FREQUENTLY("onlinejudge.submission.frequently"),
    // user
    USER_NOT_FOUND("onlinejudge.user.notfound", HttpStatus.NOT_FOUND),
    USER_SEARCH_KEYWORD_SHORT("onlinejudge.user.search.short"),
    NICK_EMPTY("onlinejudge.nick.empty"),
    NICK_LONG("onlinejudge.nick.long"),
    EMAIL_FORMAT_INCORRECT("onlinejudge.email.format.incorrect"),
    REGIST_USER_ID_EMPTY("onlinejudge.regist.user.id.empty"),
    REGIST_USER_ID_SHORT("onlinejudge.regist.user.id.short"),
    REGIST_USER_ID_LONG("onlinejudge.regist.user.id.long"),
    REGIST_USER_ID_INVALID("onlinejudge.regist.user.id.invalid"),
    EMPTY_PASSWORD("onlinejudge.password.empty"),
    PASSWORD_TOO_LONG("onlinejudge.password.long"),
    PASSWORD_TOO_SHORT("onlinejudge.password.short"),
    PASSWORD_HAS_SPACE("onlinejudge.password.space"),
    PASSWORD_NOT_CORRECT("onlinejudge.password.notcorrect"),
    PASSWORD_NOT_MATCH("onlinejudge.password.notmatch"),
    PASSWORD_INVALID_CHARACTER("onlinejudge.password.invalid"),
    // admin/user
    INVALID_EXCEL("onlinejudge.excel.invalid"),
    IMPORT_USER_EXISTS("部分用户已经存在，请移除或选择存在策略后再导入"),
    IMPORT_USER_EXISTS_CHANGE("用户已经变化，请重新导入Excel", HttpStatus.CONFLICT),
    IMPORT_USER_EMPTY("onlinejudge.import.user.empty"),
    IMPORT_USER_ID_EMPTY("onlinejudge.import.user.id.empty"),
    IMPORT_USER_RESET_PASSWORD_FORBIDDEN("由于安全原因，无法更新管理员的密码", HttpStatus.FORBIDDEN),
    RESET_PASSWORD_FORBIDDEN("由于安全原因，无法重置管理员的密码", HttpStatus.FORBIDDEN);

    private final HttpStatus status;
    private final String message;

    BusinessCode(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }

    BusinessCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.status = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
