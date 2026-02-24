package com.lagavulin.yoghee.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "AMENITY")
@Schema(description = "편의시설/제공물품")
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "AMENITY_ID")
    @Schema(description = "편의시설 ID", example = "amenity-uuid-1234")
    private String amenityId;

    @Column(name = "NAME", nullable = false, unique = true)
    @Schema(description = "제공", example = "요가매트")
    private String name;

    @Column(name = "TYPE")
    @Schema(description = "타입", example = "제공물품", allowableValues = {"제공물품", "편의시설"})
    private String type;
}