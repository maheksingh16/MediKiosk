
package com.mahek.medikiosk.repository;

import com.mahek.medikiosk.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {

}
