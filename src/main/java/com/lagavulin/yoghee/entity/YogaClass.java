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
import lombok.Setter;

@Getter
@Setter
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

    @Column(name = "`DESC`")
    private String description;

    @Column(name = "CENTER_ID")
    private String centerId;

    @Column(name = "ADDRESS_ID")
    private String addressId;

    @OneToOne
    @JoinColumn(name = "ADDRESS_ID", insertable = false, updatable = false)
    private YogaCenterAddress centerAddress;

    // DB 컬럼에 직접 저장 (YogaCenterAddress의 fullAddress 값)
    @Column(name = "ADDRESS")
    private String address;

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
