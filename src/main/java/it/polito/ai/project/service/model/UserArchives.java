package it.polito.ai.project.service.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;
@ApiModel(description = "Class representing a list of archives with HATEOAS Hyperlink.")
public class UserArchives extends ResourceSupport {
    public List<Resource<UserArchive>> getArchives() {
        return archives;
    }

    public void setArchives(List<Resource<UserArchive>> archives) {
        this.archives = archives;
    }
    @ApiModelProperty(notes = "List of archives")
    private List<Resource<UserArchive>> archives;
    public UserArchives(List<Resource<UserArchive>> resp){
        this.archives=resp;
    }
}
