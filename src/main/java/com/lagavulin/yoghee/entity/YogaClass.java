package com.lagavulin.yoghee.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CLASS")
public class YogaClass {

    @Id
    @Column(name = "CLASS_ID")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String classId;

    private String name;

    private String type;

    private int price;

    @Column(name = "DESC")
    private String description;

    @Column(name = "CENTER_ID")
    private String centerId;

    // 주소 참조 (기존 주소 컬럼 대신)
    @Column(name = "ADDRESS_ID")
    private String addressId;

    @OneToOne
    @JoinColumn(name = "ADDRESS_ID", insertable = false, updatable = false)
    private YogaCenterAddress address;

    // 기존 주소 필드들은 주석 처리 (데이터 이관 후 삭제)
    /*
    private String address;
    private double latitude;
    private double longitude;
    */

    @Column(name = "MIN_CAPACITY")
    private Long minCapacity;

    private Long capacity;

    @Column(name = "MAIN_DISPLAY")
    private String mainDisplay;

    private String thumbnail;

    @Column(name = "MASTER_ID")
    private String masterId;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @ManyToMany
    @JoinTable(
        name = "CLASS_CATEGORY",
        joinColumns = @JoinColumn(name = "CLASS_ID"),
        inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID")
    )
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "yogaClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClassFeature> features = new ArrayList<>();
}
