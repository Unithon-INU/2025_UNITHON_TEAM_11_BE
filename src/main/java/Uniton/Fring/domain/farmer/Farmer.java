package Uniton.Fring.domain.farmer;

import Uniton.Fring.domain.farmer.dto.req.UpdateStoreRequestDto;
import Uniton.Fring.domain.member.dto.req.ApplyFarmerRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "farmer")
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String representativeName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String regisNum;

    @Column(nullable = true)
    private String regisFile;

    @Column(nullable = true)
    private String passbook;

    @Column(nullable = true)
    private String certifidoc;

    @Column(nullable = false)
    private String bankNum;

    @Column(nullable = false)
    private String bank;

    @Column(nullable = false)
    private String postalAddress;

    @Column(nullable = false)
    private String defaultAddress;

    @Column(nullable = true)
    private String restAddress;

    @Builder
    private Farmer(Long memberId) {
        this.memberId = memberId;
    }

    public void updateStore(UpdateStoreRequestDto updateStoreRequestDto) {
        this.representativeName = updateStoreRequestDto.getRepresentativeName();
        this.phoneNumber = updateStoreRequestDto.getPhone();
        this.regisNum = updateStoreRequestDto.getRegistNum();
        this.postalAddress = updateStoreRequestDto.getPostalAddress();
        this.defaultAddress = updateStoreRequestDto.getDefaultAddress();
        this.restAddress = updateStoreRequestDto.getDefaultAddress();
    }

    public void applyFarmer(ApplyFarmerRequestDto applyFarmerRequestDto,
                            String registFile, String passbook, String certifidoc) {
        this.representativeName = applyFarmerRequestDto.getName();
        this.phoneNumber = applyFarmerRequestDto.getPhone();
        this.postalAddress = applyFarmerRequestDto.getPostalAddress();
        this.defaultAddress = applyFarmerRequestDto.getDefaultAddress();
        this.restAddress = applyFarmerRequestDto.getDefaultAddress();
        this.regisNum = applyFarmerRequestDto.getRegistNum();
        this.regisFile = registFile;
        this.passbook = passbook;
        this.certifidoc = certifidoc;
        this.bankNum = applyFarmerRequestDto.getBankNum();
        this.bank = applyFarmerRequestDto.getBank();
    }
}
