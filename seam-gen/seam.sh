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
taskinput3=${5}

if [ "${seamtask}" = set-properties ] 
	then
		ant -buildfile build-project-setup.xml

elif [ "${seamtask}" = new-project ] 
	then
		ant new-project -Dproject.name=${projectname}

elif [ "${seamtask}" = new-wtp-project ] 
	then
		ant new-wtp-project -Dproject.name=${projectname}

elif [ "${seamtask}" = deploy-project ] 
	then
		ant deploy-project -Dproject.name=${projectname}

elif [ "${seamtask}" = new-action ] 
	then
		ant new-stateless-action -Dproject.name=${projectname} -Daction.name=${taskinput} -Dpage.name=${taskinput2}

elif [ "${seamtask}" = new-form ] 
	then
		ant new-stateful-action -Dproject.name=${projectname} -Daction.name=${taskinput} -Dpage.name=${taskinput2}

elif [ "${seamtask}" = new-conversation ] 
	then
		ant new-conversation -Dproject.name=${projectname} -Dconversation.name=${taskinput} -Dpage.name=${taskinput2}

elif [ "${seamtask}" = new-entity ] 
	then
		ant new-entity -Dproject.name=${projectname} -Daction.name=${taskinput} -Dpage.name=${taskinput2} -DmasterPage.name=${taskinput2}

elif [ "${seamtask}" = new-mdb ] 
	then
		ant new-mdb -Dproject.name=${projectname} -Daction.name=${taskinput} 

elif [ "${seamtask}" = help ] 
	then
		cat README

else
		cat USAGE
fi