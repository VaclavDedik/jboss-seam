#/bin/sh
############################################
#
# Seam-gen tasks script. 
#
############################################

seamtask=${1}
projectname=${2}
taskinput=${3}
taskinput2=${4}

if [ "${seamtask}" = set-properties ] 
	then
		ant -buildfile build-project-setup.xml
fi

if [ "${seamtask}" = new-project ] 
	then
		ant new-project -Dproject.name=${projectname}
fi

if [ "${seamtask}" = new-wtp-project ] 
	then
		ant new-wtp-project -Dproject.name=${projectname}
fi

if [ "${seamtask}" = deploy-project ] 
	then
		ant deploy-project -Dproject.name=${projectname}
fi

if [ "${seamtask}" = new-action ] 
	then
		ant new-action -Dproject.name=${projectname} -Daction.name=${taskinput}
fi

if [ "${seamtask}" = new-slsb-action ] 
	then
		ant new-slsb-action -Dproject.name=${projectname} -Daction.name=${taskinput}
fi

if [ "${seamtask}" = new-conversation ] 
	then
		ant new-conversation -Dproject.name=${projectname} -Dconversation.name=${taskinput}
fi

if [ "${seamtask}" = new-page ] 
	then
		ant new-page -Dproject.name=${projectname} -Dpage.name=${taskinput}
fi

if [ "${seamtask}" = new-action-page ] 
	then
		ant new-action-page -Dproject.name=${projectname} -Dpage.name=${taskinput} -Daction.name=${taskinput2}
fi

if [ "${seamtask}" = new-testcase ] 
	then
		ant new-testcase -Dproject.name=${projectname} -Daction.name=${taskinput} 
fi

if [ "${seamtask}" = new-bpm-action ] 
	then
		ant new-bpm-action -Dproject.name=${projectname} -Daction.name=${taskinput} 
fi

if [ "${seamtask}" = new-entity ] 
	then
		ant new-entity -Dproject.name=${projectname} -Daction.name=${taskinput} 
fi

if [ "${seamtask}" = new-mdb ] 
	then
		ant new-mdb -Dproject.name=${projectname} -Daction.name=${taskinput} 
fi

if [ "${seamtask}" = help ] 
	then
		cat README
fi