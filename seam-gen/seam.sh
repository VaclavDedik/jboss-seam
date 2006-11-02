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

if [ "${seamtask}" = new-stateless-action ] 
	then
		ant new-slsb-action -Dproject.name=${projectname} -Daction.name=${taskinput} -Dpage.name=${taskinput2}
fi

if [ "${seamtask}" = new-conversation ] 
	then
		ant new-conversation -Dproject.name=${projectname} -Dconversation.name=${taskinput} -Dpage.name=${taskinput2}
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