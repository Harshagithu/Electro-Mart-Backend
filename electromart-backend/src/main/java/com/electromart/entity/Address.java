package com.electromart.entity;

import com.electromart.enums.AddressType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank
    @Column(nullable = false, length = 15)
    private String phone;

    @NotBlank
    @Column(name = "address_line", nullable = false, length = 255)
    private String addressLine;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String state;

    @NotBlank
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String country;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "address_type", nullable = false, length = 20)
    private AddressType addressType = AddressType.HOME;

    // Named `defaultAddress` rather than `isDefault` — Lombok generates
    // isDefaultAddress()/setDefaultAddress() cleanly either way, but a bare
    // `isDefault` field name has historically tripped people up with Lombok's
    // "is"-prefix boolean handling. @Column pins it back to `is_default` to
    // match the schema regardless of the Java field name.
    @Builder.Default
    @Column(name = "is_default", nullable = false)
    private boolean defaultAddress = false;
}