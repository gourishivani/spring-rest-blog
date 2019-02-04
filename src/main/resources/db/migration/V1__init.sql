CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL,
  `created` datetime NOT NULL,
  `last_modified` datetime DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(100) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `space_name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_users_email_idx` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



CREATE TABLE IF NOT EXISTS `post` (
  `id` bigint(20) NOT NULL,
  `created` datetime NOT NULL,
  `last_modified` datetime DEFAULT NULL,
  `description` varchar(300) NOT NULL,
  `title` varchar(30) NOT NULL,
  `author_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_author_id` (`author_id`),
  CONSTRAINT `FK_author_id` FOREIGN KEY (`author_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `comment` (
  `id` bigint(20) NOT NULL,
  `created` datetime NOT NULL,
  `last_modified` datetime DEFAULT NULL,
  `content` varchar(300) NOT NULL,
  `commentor_id` bigint(20) NOT NULL,
  `post_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_commentor_id` (`commentor_id`),
  KEY `FK_post_id` (`post_id`),
  CONSTRAINT `FK_commentor_id` FOREIGN KEY (`commentor_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FK_post_id` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;