package com.example.mirai.projectname.libraries.model;

public class Ecn extends MyChangeEvent {
	private final String contextId;

	private final String status;

	private final String title;

	public Ecn(ReleasePackage releasePackage, String rootObjectName, String contextType) {
		super(releasePackage.jsonData, rootObjectName, contextType);
		this.contextId = releasePackage.getEcnId();
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
		return "ECN";
	}
}
