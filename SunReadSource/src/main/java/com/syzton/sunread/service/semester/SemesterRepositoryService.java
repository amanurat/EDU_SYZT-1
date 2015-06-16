package com.syzton.sunread.service.semester;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.syzton.sunread.assembler.semester.SemesterAssembler;
import com.syzton.sunread.dto.semester.SemesterDTO;
import com.syzton.sunread.exception.common.DuplicateException;
import com.syzton.sunread.exception.common.NotFoundException;
import com.syzton.sunread.model.organization.Campus;
import com.syzton.sunread.model.organization.Clazz;
import com.syzton.sunread.model.semester.Semester;
import com.syzton.sunread.model.user.Student;
import com.syzton.sunread.repository.SemesterRepository;
import com.syzton.sunread.repository.organization.CampusRepository;
import com.syzton.sunread.repository.organization.ClazzRepository;
import com.syzton.sunread.repository.user.StudentRepository;

/*
 * @Date 2015-3-22
 * @Author Morgan-Leon 
 */
@Service
public class SemesterRepositoryService implements SemesterService{

    private static final Logger LOGGER = LoggerFactory.getLogger(SemesterRepositoryService.class);

    private SemesterRepository semesterRepo;
    private CampusRepository campusRepository;
    private StudentRepository studentRepository;
    private ClazzRepository clazzRepository;
    
    @Autowired
    public SemesterRepositoryService(SemesterRepository repository,CampusRepository campusRepository) {
		// TODO Auto-generated constructor stub
    	this.semesterRepo = repository;
    	this.campusRepository = campusRepository;
	}
    
    @Autowired
    public void StudentRepositoryService(StudentRepository studentRepository) {
    	this.studentRepository = studentRepository;
	}
    
    @Autowired
    public void ClazzRepositoryService(ClazzRepository clazzRepository) {
    	this.clazzRepository = clazzRepository;
	}
    
    @Transactional(rollbackFor = {DuplicateException.class})
	@Override
	public SemesterDTO add(SemesterDTO added,Long campusId) {
        LOGGER.debug("Adding a new Semester with information: {}", added);
        
        Semester exits = semesterRepo.findOne(added.getId());        
        if(exits != null){
            throw new DuplicateException("Semester with id: "+added.getId()+" is already exits..");
        }
        Campus campus = campusRepository.findOne(campusId);
        if(campus == null)
        	throw new NotFoundException("no campus found with id:"+ campusId);
        
        SemesterAssembler assembler = new SemesterAssembler();
        
        Semester model = assembler.fromDTOtoModel(added,campus);
        semesterRepo.save(model);
        added = model.createDTO();
        return added;
	}

    @Transactional(rollbackFor = {NotFoundException.class})
	@Override
	public Semester deleteById(long id) {
        LOGGER.debug("Deleting a Semester with id: {}", id);

        Semester deleted = findOne(id);
        if(deleted == null)
            throw new NotFoundException("No Semester found with id: " + id);
        
        LOGGER.debug("Deleting semester entry: {}", deleted);
        semesterRepo.delete(deleted);
        return deleted;
	}

    @Transactional(rollbackFor = {NotFoundException.class})
	@Override
	public SemesterDTO update(SemesterDTO updated) {
        LOGGER.debug("Updating contact with information: {}", updated);

        Semester model = findOne(updated.getId());
        if(model == null)
            throw new NotFoundException("No Semester found with id: " + updated.getId());
        
        SemesterAssembler assembler = new SemesterAssembler();     
        model = assembler.fromDTOtoModel(updated);
        semesterRepo.save(model);
        return updated;
	}

	@Override
	public Semester findOne(Long id) {
        LOGGER.debug("Finding a Semester with id: {}", id);

        Semester found = semesterRepo.findOne(id);
        LOGGER.debug("Found semester entry: {}", found);

        if (found == null) {
            throw new NotFoundException("No Semester found with id: " + id);
        }
        return found;
	}
	
	@Override
	public Semester findByTime(DateTime time,long campusId) {
        LOGGER.debug("Finding a Semester with id: {}", time);

        Semester found = semesterRepo.findByTime(time,campusId);
        LOGGER.debug("Found semester entry: {}", found);
        
        

        if (found == null) {
            throw new NotFoundException("No Semester found with id: " + time);
        }
        return found;
	}

	@Override
	public Page<Semester> findAll(Pageable pageable) {
        LOGGER.debug("Finding all semester entries");
        Page<Semester> semesters = semesterRepo.findAll(pageable);
        if (semesters == null) {
            throw new NotFoundException("No Semester found");
        }
        return semesters;
    }
	
	@Override
	public Page<Semester> findByCampus(long campusId,Pageable pageable) {
        LOGGER.debug("Finding all semester entries");
        Campus campus = campusRepository.findOne(campusId);
        if(campus == null)
        	throw new NotFoundException("no campus found with id:"+ campusId);
        Page<Semester> semesters = semesterRepo.findByCampus(campus, pageable);
        if (semesters == null) {
            throw new NotFoundException("No Semester found");
        }
        return semesters;
    }
	
	@Override
	public ArrayList<Semester> findByStudentId(Long studentId){
        LOGGER.debug("Finding all semester entries");
        Student student = studentRepository.findOne(studentId);
        if(student == null){
        	throw new NotFoundException("Student with ID :"+studentId);
        }
        Clazz clazz = clazzRepository.findOne(student.getClazzId());
        int grade = clazz.getGrade();
        DateTime currentTime = DateTime.now();
        DateTime fromTime = currentTime.minusYears(grade);
        ArrayList<Semester> semesters = (ArrayList<Semester>) semesterRepo.findByDuration(fromTime,currentTime,clazz.getCampus().getId());
         
        if (semesters == null) {
            throw new NotFoundException("No Semester found");
        }
        return semesters;
    }


}
