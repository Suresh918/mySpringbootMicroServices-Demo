package com.example.mirai.projectname.libraries.model;

public class Teamcenter extends MyChangeEvent {

	private String contextId;

	private String status;

	private String title;

	public Teamcenter(ReleasePackage releasePackage, String rootObjectName, String contextType) {
		super(releasePackage.getJsonData(), rootObjectName, contextType);
		this.contextId = releasePackage.getTeamcenterId();
		this.status = releasePackage.getEcnStatus();
		this.title = releasePackage.getEcnTitle();
	}

	@Override
	public String getContextId() {
		return this.contextId;
	}

	@Override
	public String getStatus() {
		return this.status;
	}

	@Override
	public String getTitle() {
		return this.title;
	}

	@Override
	public String getType() {
		return "TEAMCENTER";
	}
}
