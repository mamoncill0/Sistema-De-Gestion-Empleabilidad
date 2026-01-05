package AS.PE.domain.model;

import AS.PE.domain.model.enums.Status;

public class Project {
    private Long idProject;
    private Long ownerId;
    private String name;
    private Status status;
    private boolean deleted;

    public Project() {
    }

    public Project(Long idProject, Long ownerId, String name, Status status, boolean deleted) {
        this.idProject = idProject;
        this.ownerId = ownerId;
        this.name = name;
        this.status = status;
        this.deleted = deleted;
    }

    public Long getIdProject() {
        return idProject;
    }

    public void setIdProject(Long idProject) {
        this.idProject = idProject;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
