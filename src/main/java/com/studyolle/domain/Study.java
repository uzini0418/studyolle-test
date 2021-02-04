package com.studyolle.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Study {

	@Id @GeneratedValue
	private Long id;

	@ManyToMany
	private Set<Account> managers = new HashSet<>();

	@ManyToMany
	private Set<Account> members = new HashSet<>();

	@Column(unique = true)
	private String path;

	private String title;

	private String shortDescription;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	private String fullDescription;

	@Lob @Basic(fetch = FetchType.EAGER)
	private String image;

	@ManyToMany
	private Set<Tag> tags = new HashSet<>();

	@ManyToMany
	private Set<Zone> zones = new HashSet<>();

	private LocalDateTime publishedDateTime; // 공개일자

	private LocalDateTime closedDateTime; // 종료일자

	private LocalDateTime recruitingUpdatedDateTime; // 최근 모집 오픈시간

	private boolean recruiting; // 모집 여부

	private boolean published; // 공개 여부

	private boolean closed; // 종료 여부

	private boolean useBanner; // 배너 사용여부

	public void addManager(Account account) {
		this.managers.add(account);
	}
}