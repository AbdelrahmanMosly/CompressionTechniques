#!/bin/bash
cd "$(dirname "$0")"

assert() {                         
  E_PARAM_ERR=98
  E_ASSERT_FAILED=99

  if [ -z "$1" ] 
  then                   
    return $E_PARAM_ERR   
  fi

  assertion=$1

  if [ ! $assertion ] 
  then
    echo "❌ Test Failed."
    return 0
  else
    echo "✅ Test Passed."
    return 1
  fi  
} 

passed=0

function test {
    echo "Testing with $1"
    expected=$(sha1sum "$1" | awk '{print $1}')

    ./bin/main -c "$1"
    ./bin/main -d "$1.myZip"

    actual=$(sha1sum "$1.decompressed" | awk '{print $1}')

    echo -e "Original hash:\t\t$expected"
    echo -e "Decompressed hash:\t$actual"

    assert "$expected == $actual"

    passed=$(($passed + $?))

    echo -e "-----------------------------------"
}

echo -e "Running tests...\n"

for file in "$@"
do
    test "resources/$file"
done

echo "Tests Results: $passed/${#files[@]} passed"
echo

exit 0

