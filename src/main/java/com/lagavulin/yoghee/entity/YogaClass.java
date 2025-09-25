package com.lagavulin.yoghee.entity;

import java.util.HashSet;
import java.util.Set;

import com.lagavulin.yoghee.model.dto.YogaClassDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
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

    @Column(name = "STORE_ID")
    private String storeId;

    private String address;

    private double latitude;

    private double longitude;

    private int capacity;

    @Column(name = "MAIN_DISPLAY")
    private String mainDisplay;

    private String thumbnail;

    @Column(name = "MASTER_ID")
    private String masterId;

    @ManyToMany
    @JoinTable(
        name = "CLASS_CATEGORY",
        joinColumns = @JoinColumn(name = "CLASS_ID"),
        inverseJoinColumns = @JoinColumn(name = "CATEGORY_ID")
    )
    private Set<Category> categories = new HashSet<>();

    public YogaClassDto toDto(){
        return YogaClassDto.builder()
                           .className(name)
                           .classId(classId)
                           .type(type)
                           .address(address)
                           .description(description)
                           .price(price)
                           .latitude(latitude)
                           .longitude(longitude)
                           .thumbnail(thumbnail)
                           .capacity(capacity)
                           .build();
    }
}
