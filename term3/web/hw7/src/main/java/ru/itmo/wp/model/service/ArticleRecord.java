package ru.itmo.wp.model.service;

import java.util.Date;

public record ArticleRecord(String title, String text, Date creationTime, String userName) {
}
